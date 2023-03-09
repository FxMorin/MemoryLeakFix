package ca.fxco.memoryleakfix.mixin.hugeScreenshotLeak;

import ca.fxco.memoryleakfix.config.MinecraftRequirement;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.blaze3d.platform.GlUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;

@MinecraftRequirement(minVersion = "1.17")
@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public abstract class Minecraft_screenshotMixin {

    @Nullable
    private ByteBuffer memoryLeakFix$screenshotByteBuffer;

    @ModifyExpressionValue(
            method = "grabHugeScreenshot",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/GlUtil;allocateMemory(I)Ljava/nio/ByteBuffer;"
            )
    )
    private ByteBuffer memoryLeakFix$captureByteBuffer(ByteBuffer byteBuf) {
        return this.memoryLeakFix$screenshotByteBuffer = byteBuf;
    }

    @Inject(
            method = "grabHugeScreenshot",
            at = @At(
                    value = "CONSTANT",
                    args = "stringValue=screenshot.failure"
            )
    )
    private void memoryLeakFix$freeByteBuffer(CallbackInfoReturnable<Component> cir) {
        GlUtil.freeMemory(this.memoryLeakFix$screenshotByteBuffer);
        this.memoryLeakFix$screenshotByteBuffer = null;
    }
}
