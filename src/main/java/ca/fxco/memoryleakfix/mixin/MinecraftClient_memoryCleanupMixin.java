package ca.fxco.memoryleakfix.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClient_memoryCleanupMixin {

    /**
     * Cleans up memory once a server closes, since large amounts of allocated memory will stay
     * in ram until a full garbage collection is done.
     */

    @Inject(method= "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V",at=@At("RETURN"))
    public void disconnect(Screen screen, CallbackInfo ci) {System.gc();}
}
