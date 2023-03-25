package ca.fxco.memoryleakfix.mixin.targetEntityLeak;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public abstract class MinecraftClient_targetClearMixin {

    @Shadow
    @Nullable
    public Entity crosshairPickEntity;

    @Shadow
    @Nullable
    public HitResult hitResult;


    @Inject(
            method = "updateScreenAndTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;runTick(Z)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void memoryLeakFix$resetTarget(CallbackInfo ci) {
        this.crosshairPickEntity = null;
        this.hitResult = null;
    }
}
