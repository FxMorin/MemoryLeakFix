package ca.fxco.memoryleakfix.config;

import ca.fxco.memoryleakfix.MemoryLeakFix;
import ca.fxco.memoryleakfix.MemoryLeakFixBootstrap;
import ca.fxco.memoryleakfix.MemoryLeakFixExpectPlatform;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MemoryLeakFixMixinConfigPlugin implements IMixinConfigPlugin {

    private static final Object2BooleanMap<String> MIXIN_CLASS_CACHE = new Object2BooleanArrayMap<>();
    private static final Set<String> APPLIED_MEMORY_LEAK_FIXES = new HashSet<>();
    private static boolean shouldMentionFixCount = true;

    @Override
    public void onLoad(String mixinPackage) {
        MixinExtrasBootstrap.init();
        MemoryLeakFixBootstrap.init();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
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

                String[] groups = APPLIED_MEMORY_LEAK_FIXES.toArray(new String[]{});
                StringBuilder fixesBuilder = new StringBuilder();
                fixesBuilder.append("[MemoryLeakFix] Currently enabled memory leak fixes: [");
                for (int i = 0; i < size; i++) {
                    fixesBuilder.append(groups[i]);
                    if (i != size - 1) {
                        fixesBuilder.append(", ");
                    }
                }
                MemoryLeakFix.LOGGER.info(fixesBuilder.append("]"));
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

    public static void runMixinClassNodeRequirements(String className, ClassNode classNode) {
        if (!MIXIN_CLASS_CACHE.computeIfAbsent(className, className2 ->
                areRequirementsMet(Annotations.getInvisible(classNode, MinecraftRequirement.class))
        )) {
            return;
        }
        Iterator<MethodNode> methodIterator = classNode.methods.iterator();
        while (methodIterator.hasNext()) {
            MethodNode node = methodIterator.next();
            AnnotationNode requirements = Annotations.getInvisible(node, MinecraftRequirement.class);
            if (requirements != null) {
                for (AnnotationNode versionRange : (Iterable<AnnotationNode>) Annotations.getValue(requirements)) {
                    if (!isVersionRangeValid(versionRange)) {
                        methodIterator.remove();
                    }
                }
            }
        }
        Iterator<FieldNode> fieldIterator = classNode.fields.iterator();
        while (fieldIterator.hasNext()) {
            FieldNode node = fieldIterator.next();
            AnnotationNode requirements = Annotations.getInvisible(node, MinecraftRequirement.class);
            if (requirements != null) {
                for (AnnotationNode versionRange : (Iterable<AnnotationNode>) Annotations.getValue(requirements)) {
                    if (!isVersionRangeValid(versionRange)) {
                        fieldIterator.remove();
                    }
                }
            }
        }
    }

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
}
