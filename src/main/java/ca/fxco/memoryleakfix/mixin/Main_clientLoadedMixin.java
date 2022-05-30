package ca.fxco.memoryleakfix.mixin;

import ca.fxco.memoryleakfix.memoryLeakFix;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Main.class)
public class Main_clientLoadedMixin {


    @Redirect(
            method = "main([Ljava/lang/String;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/MinecraftClient;shouldRenderAsync()Z"
            )
    )
    private static boolean loadAllMixinsThenShouldRenderAsync(MinecraftClient instance) {
        memoryLeakFix.forceLoadAllMixinsAndClearSpongePoweredCache();
        return instance.shouldRenderAsync();
    }
}
