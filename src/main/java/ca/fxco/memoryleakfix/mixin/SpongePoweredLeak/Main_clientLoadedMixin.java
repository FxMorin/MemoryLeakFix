package ca.fxco.memoryleakfix.mixin.SpongePoweredLeak;

import ca.fxco.memoryleakfix.MemoryLeakFix;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.main.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
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
        MemoryLeakFix.forceLoadAllMixinsAndClearSpongePoweredCache();
        return instance.shouldRenderAsync();
    }
}
