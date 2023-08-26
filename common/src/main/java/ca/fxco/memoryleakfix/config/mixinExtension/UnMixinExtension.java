package ca.fxco.memoryleakfix.config.mixinExtension;

import ca.fxco.memoryleakfix.config.MemoryLeakFixMixinConfigPlugin;
import ca.fxco.memoryleakfix.utils.MixinInternals;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ext.IExtension;
import org.spongepowered.asm.mixin.transformer.ext.ITargetClassContext;

/**
 * Has the ability to remove mixins from Mixin Classes before they are used
 */
public class UnMixinExtension implements IExtension {
    @Override
    public boolean checkActive(MixinEnvironment environment) {
        return true;
    }

    @Override
    public void preApply(ITargetClassContext context) {
        for (Pair<IMixinInfo, ClassNode> pair : MixinInternals.getMixinsFor(context)) {
            MemoryLeakFixMixinConfigPlugin.runCustomMixinClassNodeAnnotations(pair.getLeft().getClassName(), pair.getRight());
        }
    }

    @Override
    public void postApply(ITargetClassContext context) {}

    @Override
    public void export(MixinEnvironment env, String name, boolean force, ClassNode classNode) {}
}
