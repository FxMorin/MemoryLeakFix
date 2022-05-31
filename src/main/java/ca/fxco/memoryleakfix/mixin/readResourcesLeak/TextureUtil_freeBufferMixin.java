package ca.fxco.memoryleakfix.mixin.readResourcesLeak;

import com.mojang.blaze3d.platform.TextureUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

@Environment(EnvType.CLIENT)
@Mixin(value = TextureUtil.class, remap = false)
public class TextureUtil_freeBufferMixin {

    /*
     * This fixes memory leaks under 2 conditions. If you are reloading a pack and it crashes, or if a mod changes how
     * textures are loaded and a specific texture keeps crashing. Both of which can be drastically large memory leaks.
     * For example, if I load a 4k resource pack a single texture failing could lose me 3.9mb This happens any time the
     * textures are reloaded. If it's the latter, it could constantly be leaking some textures.
     *
     * By Fx Morin - thanks to Icyllis Milica for [MC-226729](https://bugs.mojang.com/browse/MC-226729)
     */


    @Inject(
            method = "readResource(Ljava/io/InputStream;)Ljava/nio/ByteBuffer;",
            locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/system/MemoryUtil;memAlloc(I)Ljava/nio/ByteBuffer;",
                    shift = At.Shift.BY,
                    by = 2,
                    ordinal = 0
            ),
            cancellable = true
    )
    private static void readResourceWithoutLeak1(InputStream inputStream, CallbackInfoReturnable<ByteBuffer> cir,
                                                ByteBuffer byteBuf, FileInputStream fileInputStream,
                                                FileChannel fileChannel) throws IOException {
        try {
            while(fileChannel.read(byteBuf) != -1) {}
        } catch (IOException e) {
            MemoryUtil.memFree(byteBuf);
            throw e;
        }
        cir.setReturnValue(byteBuf);
    }


    @Inject(
            method = "readResource(Ljava/io/InputStream;)Ljava/nio/ByteBuffer;",
            locals = LocalCapture.CAPTURE_FAILHARD,
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/nio/channels/Channels;newChannel(Ljava/io/InputStream;)" +
                            "Ljava/nio/channels/ReadableByteChannel;",
                    shift = At.Shift.BY,
                    by = 2,
                    ordinal = 0
            ),
            cancellable = true
    )
    private static void readResourceWithoutLeak2(InputStream inputStream, CallbackInfoReturnable<ByteBuffer> cir,
                                                 ByteBuffer byteBuf,
                                                 ReadableByteChannel readableByteChannel) throws IOException {
        try {
            while(readableByteChannel.read(byteBuf) != -1)
                if (byteBuf.remaining() == 0) byteBuf = MemoryUtil.memRealloc(byteBuf, byteBuf.capacity() * 2);
        } catch (IOException e) {
            MemoryUtil.memFree(byteBuf);
            throw e;
        }
        cir.setReturnValue(byteBuf);
    }
}
