package ca.fxco.memoryleakfix.mixin.hugeScreenshotLeak;

import com.mojang.blaze3d.platform.GlDebugInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.nio.ByteBuffer;

@Mixin(MinecraftClient.class)
public class MinecraftClient_screenshotMixin {

    private ByteBuffer byteBuffer;


    @Inject(
            method = "takeHugeScreenshot",
            at = @At("HEAD")
    )
    private void createByteBuffer(File gameDirectory, int unitWidth, int unitHeight, int width, int height, CallbackInfoReturnable<Text> cir) {
        this.byteBuffer = GlDebugInfo.allocateMemory(unitWidth * unitHeight * 3);
    }


    @Redirect(
            method = "takeHugeScreenshot",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/GlDebugInfo;allocateMemory(I)Ljava/nio/ByteBuffer;"
            )
    )
    private ByteBuffer setNewByteBuffer(int size) {
        return this.byteBuffer;
    }


    @Inject(
            method = "takeHugeScreenshot",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Throwable;)V"
            )
    )
    private void freeByteBuffer(File gameDirectory, int unitWidth, int unitHeight, int width, int height, CallbackInfoReturnable<Text> cir) {
        GlDebugInfo.freeMemory(this.byteBuffer);
    }
}
