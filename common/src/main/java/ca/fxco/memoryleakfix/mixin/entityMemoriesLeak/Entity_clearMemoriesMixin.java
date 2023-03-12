package ca.fxco.memoryleakfix.mixin.entityMemoriesLeak;

import ca.fxco.memoryleakfix.config.MinecraftRequirement;
import ca.fxco.memoryleakfix.config.VersionRange;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MinecraftRequirement(@VersionRange(maxVersion = "1.19.3"))
@Mixin(Entity.class)
public abstract class Entity_clearMemoriesMixin {

    @Group(name = "memoryLeakFix$onEntityRemoved", min = 1, max = 1)
    @Inject(method = "remove", at = @At("TAIL"))
    protected void memoryLeakFix$OnEntityRemoved(CallbackInfo ci) {
    }

    // method_5650 (Fabric) and m_142687_ (Forge) are the intermediary names which are needed because in older Minecraft versions the parameters of the method were different
    @Group(name = "memoryLeakFix$onEntityRemoved", min = 1, max = 1)
    @SuppressWarnings({"UnresolvedMixinReference", "MixinAnnotationTarget"})
    @Inject(method = {"method_5650", "m_142687_"}, at = @At("TAIL"), remap = false)
    protected void memoryLeakFix$OnEntityRemoved_inOlderVersions(CallbackInfo ci) {
        this.memoryLeakFix$OnEntityRemoved(ci);
    }
}
