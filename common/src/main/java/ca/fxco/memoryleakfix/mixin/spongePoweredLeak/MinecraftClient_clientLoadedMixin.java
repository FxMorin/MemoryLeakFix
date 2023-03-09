package ca.fxco.memoryleakfix.mixin.spongePoweredLeak;

import ca.fxco.memoryleakfix.MemoryLeakFix;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class MinecraftClient_clientLoadedMixin {


    @Inject(
            method = "run",
            at = @At("HEAD")
    )
    private void memoryLeakFix$loadAllMixinsClientSide(CallbackInfo ci) {
        MemoryLeakFix.forceLoadAllMixinsAndClearSpongePoweredCache();
    }
}
