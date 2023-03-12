package ca.fxco.memoryleakfix.mixin.readResourcesLeak;

import ca.fxco.memoryleakfix.config.MinecraftRequirement;
import ca.fxco.memoryleakfix.config.VersionRange;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import java.nio.ByteBuffer;
import java.nio.channels.Channel;

// class_4536 is the intermediary class name of TextureUtil in 1.15.0 - 1.16.5,
// where the class was part of the net.minecraft package, method_24962 is the equivalent of readResource
@MinecraftRequirement(@VersionRange(minVersion = "1.15.0", maxVersion = "1.16.5"))
@Pseudo
@Environment(EnvType.CLIENT)
@Mixin(targets = "net/minecraft/class_4536", remap = false)
public abstract class TextureUtil_freeBuffer_MinecraftPackageMixin {

    /*
     * This fixes memory leaks under 2 conditions. If you are reloading a pack and it crashes, or if a mod changes how
     * textures are loaded and a specific texture keeps crashing. Both of which can be drastically large memory leaks.
     * For example, if I load a 4k resource pack a single texture failing could lose me 3.9mb This happens any time the
     * textures are reloaded. If it's the latter, it could constantly be leaking some textures.
     *
     * By Fx Morin - thanks to Icyllis Milica for [MC-226729](https://bugs.mojang.com/browse/MC-226729)
     */

    @WrapOperation(
            method = "method_24962",
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
            remap = false
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
