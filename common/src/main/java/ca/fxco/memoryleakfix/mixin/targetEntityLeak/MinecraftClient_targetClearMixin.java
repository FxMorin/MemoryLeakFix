package ca.fxco.memoryleakfix.mixin.targetEntityLeak;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class MinecraftClient_targetClearMixin {

    @Shadow
    @Nullable
    public Entity targetedEntity;

    @Shadow
    @Nullable
    public HitResult crosshairTarget;


    @Inject(
            method = "reset",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/MinecraftClient;render(Z)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void memoryLeakFix$resetTarget(CallbackInfo ci) {
        this.targetedEntity = null;
        this.crosshairTarget = null;
    }
}
