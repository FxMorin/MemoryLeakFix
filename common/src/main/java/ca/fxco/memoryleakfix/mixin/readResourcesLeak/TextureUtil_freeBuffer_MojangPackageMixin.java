package ca.fxco.memoryleakfix.mixin.readResourcesLeak;

import ca.fxco.memoryleakfix.config.MinecraftRequirement;
import ca.fxco.memoryleakfix.config.VersionRange;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.TextureUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import java.nio.ByteBuffer;
import java.nio.channels.Channel;

@MinecraftRequirement({
        @VersionRange(maxVersion = "1.14.4"),
        @VersionRange(minVersion = "1.17.0")
})
@Environment(EnvType.CLIENT)
@Mixin(value = TextureUtil.class, remap = false)
public abstract class TextureUtil_freeBuffer_MojangPackageMixin {

    /*
     * This fixes memory leaks under 2 conditions. If you are reloading a pack and it crashes, or if a mod changes how
     * textures are loaded and a specific texture keeps crashing. Both of which can be drastically large memory leaks.
     * For example, if I load a 4k resource pack a single texture failing could lose me 3.9mb This happens any time the
     * textures are reloaded. If it's the latter, it could constantly be leaking some textures.
     *
     * By Fx Morin - thanks to Icyllis Milica for [MC-226729](https://bugs.mojang.com/browse/MC-226729)
     */

    @SuppressWarnings("DefaultAnnotationParam")
    @WrapOperation(
            method = "readResource(Ljava/io/InputStream;)Ljava/nio/ByteBuffer;",
            at = {
                    @At(
                            value = "INVOKE",
                            target = "Ljava/nio/channels/FileChannel;read(Ljava/nio/ByteBuffer;)I"
                    ),
                    @At(
                            value = "INVOKE",
                            target = "Ljava/nio/channels/ReadableByteChannel;read(Ljava/nio/ByteBuffer;)I"
                    )
            },
            remap = true
    )
    private static int memoryLeakFix$readResourceWithoutLeak(@Coerce Channel channel, ByteBuffer byteBuf,
                                                             Operation<Integer> original) {
        try {
            return original.call(channel, byteBuf);
        } catch (Exception e) {
            MemoryUtil.memFree(byteBuf);
            throw e;
        }
    }
}
