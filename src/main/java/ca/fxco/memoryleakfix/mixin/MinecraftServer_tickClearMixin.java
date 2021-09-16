package ca.fxco.memoryleakfix.mixin;

import ca.fxco.memoryleakfix.memoryLeakFix;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;


@Mixin(MinecraftServer.class)
public abstract class MinecraftServer_tickClearMixin {

    /**
     * If on the server, then we want to tick clearing the NO_GROUP of spongepowered here
     */

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(BooleanSupplier booleanSupplier_1, CallbackInfo ci) {
        memoryLeakFix.members.clear();
    }
}