package ca.fxco.memoryleakfix.config;

import ca.fxco.memoryleakfix.MemoryLeakFix;
import ca.fxco.memoryleakfix.MemoryLeakFixBootstrap;
import ca.fxco.memoryleakfix.MemoryLeakFixExpectPlatform;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.*;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

import java.lang.annotation.Annotation;
import java.util.*;

public class MemoryLeakFixMixinConfigPlugin implements IMixinConfigPlugin {

    private static final Object2BooleanMap<String> MIXIN_CLASS_CACHE = new Object2BooleanArrayMap<>();
    private static final Set<String> APPLIED_MEMORY_LEAK_FIXES = new HashSet<>();
    private static boolean shouldMentionFixCount = true;

    // prevent running custom annotations twice
    private static final Set<String> CUSTOM_ANNOTATION_CLASSES = new HashSet<>();

    private static final boolean VERBOSE = false;

    @Override
    public void onLoad(String mixinPackage) {
        MixinExtrasBootstrap.init();
        MemoryLeakFixBootstrap.init();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!mixinClassName.startsWith("ca.fxco.memoryleakfix")) {
            return true;
        }
        boolean shouldApply = MIXIN_CLASS_CACHE.computeIfAbsent(mixinClassName, mixinClassName2 ->
            areRequirementsMet(getMinecraftRequirement((String) mixinClassName2))
        );
        if (shouldApply) {
            String classGroup = mixinClassName.substring(0, mixinClassName.lastIndexOf("."));
            APPLIED_MEMORY_LEAK_FIXES.add(classGroup.substring(classGroup.lastIndexOf(".") + 1));
        }
        return shouldApply;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        if (shouldMentionFixCount) { // This runs after all shouldApplyMixin() checks have passed
            shouldMentionFixCount = false;

            int size = APPLIED_MEMORY_LEAK_FIXES.size();
            if (size > 0) {
                MemoryLeakFix.LOGGER.info("[MemoryLeakFix] Will be applying " + size + " memory leak fixes!");
                MemoryLeakFix.LOGGER.info("[MemoryLeakFix] Currently enabled memory leak fixes: " + APPLIED_MEMORY_LEAK_FIXES);
            }
        }
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @SuppressWarnings("unchecked")
    public static void runCustomMixinClassNodeAnnotations(String className, ClassNode classNode) {
        if (!className.startsWith("ca.fxco.memoryleakfix")) {
            return;
        }
        if (CUSTOM_ANNOTATION_CLASSES.contains(className)) {
            return;
        }
        CUSTOM_ANNOTATION_CLASSES.add(className);
        if (!MIXIN_CLASS_CACHE.computeIfAbsent(className, className2 ->
                areRequirementsMet(Annotations.getInvisible(classNode, MinecraftRequirement.class))
        )) {
            return;
        }
        Iterator<MethodNode> methodIterator = classNode.methods.iterator();
        while (methodIterator.hasNext()) {
            MethodNode node = methodIterator.next();
            boolean isValid = true;
            AnnotationNode requirements = Annotations.getInvisible(node, MinecraftRequirement.class);
            if (requirements != null) {
                for (AnnotationNode versionRange : (Iterable<AnnotationNode>) Annotations.getValue(requirements)) {
                    if (!isVersionRangeValid(versionRange)) {
                        methodIterator.remove();
                        isValid = false;
                        break;
                    }
                }
            }
            if (isValid) {
                AnnotationNode remapTarget = Annotations.getVisible(node, RemapTarget.class);
                if (remapTarget != null) {
                    executeRemapAnnotation(node, Annotations.getValue(remapTarget, "value"), false);
                    executeRemapAnnotation(node, Annotations.getValue(remapTarget, "target"), true);
                } else {
                    executeRemapAnnotation(node, Annotations.getVisible(node, Remap.class), false);
                }
            }
        }
        Map<FieldNode, String> fieldsRemapping = new HashMap<>();
        Iterator<FieldNode> fieldIterator = classNode.fields.iterator();
        while (fieldIterator.hasNext()) {
            FieldNode node = fieldIterator.next();
            boolean isValid = true;
            AnnotationNode requirements = Annotations.getInvisible(node, MinecraftRequirement.class);
            if (requirements != null) {
                for (AnnotationNode versionRange : (Iterable<AnnotationNode>) Annotations.getValue(requirements)) {
                    if (!isVersionRangeValid(versionRange)) {
                        fieldIterator.remove();
                        isValid = false;
                        break;
                    }
                }
            }
            if (isValid) {
                AnnotationNode annotationNode = Annotations.getVisible(node, Remap.class);
                if (annotationNode != null && Annotations.getVisible(node, Shadow.class) == null) {
                    List<String> mappings = Annotations.getValue(annotationNode, MemoryLeakFixExpectPlatform.getMappingType());
                    if (mappings != null && mappings.size() >= 1) {
                        if (mappings.size() > 1) {
                            throw new IllegalStateException("Fields cannot contain more than 1 remap! - " + className + " - " + node.name + node.desc);
                        }
                        String mapping = mappings.get(0);
                        fieldsRemapping.put(node, mapping);
                        if (VERBOSE) {
                            System.out.println("Remapping field: " + node.name + " -> " + mapping);
                        }
                        node.name = mapping;
                    }
                }
            }
        }
        if (fieldsRemapping.size() > 0) {
            // Batch all the field changes together, so we only need to do the method search once
            executeRemapAnnotation(className, classNode, fieldsRemapping);
        }
    }

    @SuppressWarnings("unchecked")
    private static boolean areRequirementsMet(@Nullable AnnotationNode requirements) {
        if (requirements != null) {
            for (AnnotationNode versionRange : (Iterable<AnnotationNode>) Annotations.getValue(requirements)) {
                if (isVersionRangeValid(versionRange)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    @Nullable
    private static AnnotationNode getMinecraftRequirement(String mixinClassName) {
        try {
            return Annotations.getInvisible(MixinService.getService().getBytecodeProvider().getClassNode(mixinClassName), MinecraftRequirement.class);
        } catch (Exception e) {
            return null;
        }
    }

    private static boolean isVersionRangeValid(AnnotationNode versionRange) {
        String minVersion = Annotations.getValue(versionRange, "minVersion");
        if (minVersion != null && !minVersion.isEmpty()) {
            if (MemoryLeakFixExpectPlatform.compareMinecraftToVersion(minVersion) < 0) {
                return false;
            }
        }
        String maxVersion = Annotations.getValue(versionRange, "maxVersion");
        if (maxVersion != null && !maxVersion.isEmpty()) {
            if (MemoryLeakFixExpectPlatform.compareMinecraftToVersion(maxVersion) > 0) {
                return false;
            }
        }
        return true;
    }

    private static void executeRemapAnnotation(String className, ClassNode classNode, Map<FieldNode, String> fieldRemapping) {
        className = className.replace(".", "/");
        for (MethodNode methodNode : classNode.methods) {
            if (VERBOSE) {
                System.out.println("Looking through method: " + methodNode.name + methodNode.desc);
            }
            for (ListIterator<AbstractInsnNode> it = methodNode.instructions.iterator(); it.hasNext(); ) {
                AbstractInsnNode insn = it.next();
                if (insn instanceof FieldInsnNode && ((FieldInsnNode) insn).owner.equals(className)) {
                    FieldInsnNode fieldInsn = (FieldInsnNode) insn;
                    for (Map.Entry<FieldNode, String> entry : fieldRemapping.entrySet()) {
                        FieldNode node = entry.getKey();
                        if (node.name.equals(fieldInsn.name) && node.desc.equals(fieldInsn.desc)) {
                            if (VERBOSE) {
                                System.out.println("Found matching field: " + node.name + node.desc + " - replacing with: " + entry.getValue());
                            }
                            it.remove();
                            it.add(new FieldInsnNode(fieldInsn.getOpcode(), fieldInsn.owner, entry.getValue(), fieldInsn.desc));
                        }
                    }
                }
            }
        }
    }

    private static void executeRemapAnnotation(MethodNode node, @Nullable AnnotationNode remap, boolean target) {
        if (remap == null) {
            return;
        }
        boolean excludeDev = Annotations.getValue(remap, "excludeDev");
        if (excludeDev && MemoryLeakFixExpectPlatform.isDevEnvironment()) {
            if (VERBOSE) {
                System.out.println("Remap annotation excluded from dev: " + node.name + node.desc);
            }
            return;
        }
        if (VERBOSE) {
            System.out.println("Remap annotation found for: " + node.name + node.desc);
        }
        List<String> mapping = Annotations.getValue(remap, MemoryLeakFixExpectPlatform.getMappingType());
        if (mapping != null && mapping.size() > 0) {
            if (VERBOSE) {
                System.out.println("Attempting to remap method to: " + mapping);
            }
            if (target) {
                remapMixinAnnotationTarget(node, mapping);
            } else {
                remapMixinAnnotation(node, mapping);
            }
        }
    }

    private static void remapMixinAnnotation(MethodNode node, List<String> remapped) {
        AnnotationNode annotationNode = getMixinAnnotation(node);
        if (annotationNode != null) {
            if (VERBOSE) {
                System.out.println("Mixin annotation was found: " + annotationNode.desc + " - Remapping: " + annotationNode.values);
            }
            for (int i = 0; i < annotationNode.values.size() - 1; i++) {
                Object obj = annotationNode.values.get(i);
                if (obj instanceof String && obj.equals("method")) {
                    List<String> methodList = (List<String>)annotationNode.values.get(i + 1);
                    if (VERBOSE) {
                        System.out.println("Original methods: " + methodList);
                    }

                    annotationNode.values.set(i + 1, remapped);
                }
            }
        }
    }

    private static void remapMixinAnnotationTarget(MethodNode node, List<String> remapped) {
        AnnotationNode annotationNode = getMixinAnnotation(node);
        if (annotationNode != null) {
            int count = 0;
            for (int i = 0; i < annotationNode.values.size(); i++) {
                Object obj = annotationNode.values.get(i);
                if (obj instanceof At[]) {
                    At[] ats = (At[])obj;
                    for (At at : ats) {
                        annotationNode.values.set(i, alterAtTarget(at, remapped.get(count)));
                        count++;
                    }
                } else if (obj instanceof At) {
                    annotationNode.values.set(i, alterAtTarget((At)obj, remapped.get(count)));
                    count++;
                }
            }
        }
    }

    private static @Nullable AnnotationNode getMixinAnnotation(MethodNode node) {
        return Annotations.getSingleVisible(
                node,
                Inject.class,
                Redirect.class,
                ModifyArg.class,
                ModifyArgs.class,
                ModifyConstant.class,
                ModifyVariable.class,
                // MixinExtras
                ModifyExpressionValue.class,
                ModifyReceiver.class,
                ModifyReturnValue.class,
                WrapOperation.class,
                WrapWithCondition.class
        );
    }

    private static At alterAtTarget(At at, String target) {
        return new At() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return at.annotationType();
            }
            @Override
            public String id() {
                return at.id();
            }
            @Override
            public String value() {
                return at.value();
            }
            @Override
            public String slice() {
                return at.slice();
            }
            @Override
            public Shift shift() {
                return at.shift();
            }
            @Override
            public int by() {
                return at.by();
            }
            @Override
            public String[] args() {
                return at.args();
            }
            @Override
            public String target() {
                return target;
            }
            @Override
            public Desc desc() {
                return at.desc();
            }
            @Override
            public int ordinal() {
                return at.ordinal();
            }
            @Override
            public int opcode() {
                return at.opcode();
            }
            @Override
            public boolean remap() {
                return at.remap();
            }
        };
    }
}
