package ca.fxco.memoryleakfix.mixin.spongePoweredLeak;

import ca.fxco.memoryleakfix.fabric.MemoryLeakFixFabric;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.SERVER)
@Mixin(MinecraftServer.class)
public abstract class MinecraftServer_serverLoadedMixin {


    @Inject(
            method = "loadLevel",
            at = @At("RETURN")
    )
    private void memoryLeakFix$loadAllMixinsServerSide(CallbackInfo ci) {
        MemoryLeakFixFabric.forceLoadAllMixinsAndClearSpongePoweredCache();
    }
}
