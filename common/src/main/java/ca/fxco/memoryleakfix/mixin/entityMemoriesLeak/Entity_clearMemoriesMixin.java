package ca.fxco.memoryleakfix.mixin.entityMemoriesLeak;

import ca.fxco.memoryleakfix.config.MinecraftRequirement;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MinecraftRequirement(maxVersion = "1.19.3")
@Mixin(Entity.class)
public abstract class Entity_clearMemoriesMixin {

    // method_5650 is the intermediary name which is needed because in older Minecraft versions the parameters of the method were different
    // we require 0 because using the intermediary name will probably cause issues on older minecraft versions with forge, and we simply don't care enough atm ¯\_(ツ)_/¯
    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = {"remove", "method_5650"}, at = @At("TAIL"), require = 0, allow = 1)
    protected void memoryLeakFix$OnEntityRemoved(CallbackInfo ci) {
    }
}
