package ca.fxco.memoryleakfix.mixin.spongePoweredLeak;

import ca.fxco.memoryleakfix.fabric.MemoryLeakFixFabric;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public abstract class Minecraft_clientLoadedMixin {


    @Inject(
            method = "run",
            at = @At("HEAD")
    )
    private void memoryLeakFix$loadAllMixinsClientSide(CallbackInfo ci) {
        MemoryLeakFixFabric.forceLoadAllMixinsAndClearSpongePoweredCache();
    }
}
