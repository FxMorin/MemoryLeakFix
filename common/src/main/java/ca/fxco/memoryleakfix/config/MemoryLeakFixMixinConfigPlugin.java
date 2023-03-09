package ca.fxco.memoryleakfix.config;

import ca.fxco.memoryleakfix.MemoryLeakFixExpectPlatform;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

import java.util.List;
import java.util.Set;

public class MemoryLeakFixMixinConfigPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {
        MixinExtrasBootstrap.init();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        AnnotationNode annotationNode = getMinecraftRequirement(mixinClassName);
        if (annotationNode != null) {
            String minVersion = Annotations.getValue(annotationNode, "minVersion");
            if (minVersion != null && !minVersion.isEmpty()) {
                if (MemoryLeakFixExpectPlatform.compareMinecraftToVersion(minVersion) < 0) {
                    return false;
                }
            }
            String maxVersion = Annotations.getValue(annotationNode, "maxVersion");
            if (maxVersion != null && !maxVersion.isEmpty()) {
                if (MemoryLeakFixExpectPlatform.compareMinecraftToVersion(maxVersion) > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
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

    @Nullable
    private static AnnotationNode getMinecraftRequirement(String mixinClassName) {
        try {
            return Annotations.getInvisible(MixinService.getService().getBytecodeProvider().getClassNode(mixinClassName), MinecraftRequirement.class);
        } catch (Exception e) {
            return null;
        }
    }
}
