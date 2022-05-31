package ca.fxco.memoryleakfix.mixin.SpongePoweredLeak;

import ca.fxco.memoryleakfix.MemoryLeakFix;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.SERVER)
@Mixin(MinecraftServer.class)
public class MinecraftServer_loadWorldMixin {


    @Inject(
            method = "loadWorld",
            at = @At("RETURN")
    )
    private void onFinishedLoadingWorlds(CallbackInfo ci) {
        MemoryLeakFix.forceLoadAllMixinsAndClearSpongePoweredCache();
    }
}
