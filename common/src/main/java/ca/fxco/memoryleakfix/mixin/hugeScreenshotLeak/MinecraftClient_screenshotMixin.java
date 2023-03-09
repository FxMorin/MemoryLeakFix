package ca.fxco.memoryleakfix.mixin.hugeScreenshotLeak;

import ca.fxco.memoryleakfix.config.MinecraftRequirement;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.platform.GlDebugInfo;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

@MinecraftRequirement(minVersion = "1.17")
@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class MinecraftClient_screenshotMixin {

    @Unique
    @Nullable
    private ByteBuffer memoryLeakFix$screenshotByteBuffer;


    @ModifyExpressionValue(
            method = "takeHugeScreenshot",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/GlDebugInfo;allocateMemory(I)Ljava/nio/ByteBuffer;"
            )
    )
    private ByteBuffer memoryLeakFix$captureByteBuffer(ByteBuffer byteBuf) {
        return this.memoryLeakFix$screenshotByteBuffer = byteBuf;
    }


    @Inject(
            method = "takeHugeScreenshot",
            at = @At(
                    value = "CONSTANT",
                    args = "stringValue=screenshot.failure"
            )
    )
    private void memoryLeakFix$freeByteBuffer(CallbackInfoReturnable<Text> cir) {
        GlDebugInfo.freeMemory(this.memoryLeakFix$screenshotByteBuffer);
        this.memoryLeakFix$screenshotByteBuffer = null;
    }
}
