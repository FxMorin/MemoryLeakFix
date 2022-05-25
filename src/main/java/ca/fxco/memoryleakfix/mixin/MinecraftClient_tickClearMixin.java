package ca.fxco.memoryleakfix.mixin;

import ca.fxco.memoryleakfix.memoryLeakFix;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClient_tickClearMixin {

    /**
     * If on the client, then we want to tick clearing the NO_GROUP of spongepowered here
     */


    @Inject(
            method = "render(Z)V",
            at = @At("HEAD")
    )
    private void onTick(boolean tick, CallbackInfo ci) {
        memoryLeakFix.onTick();
    }
}