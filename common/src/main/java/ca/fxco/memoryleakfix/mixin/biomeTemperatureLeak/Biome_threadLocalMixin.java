package ca.fxco.memoryleakfix.mixin.biomeTemperatureLeak;

import ca.fxco.memoryleakfix.config.MinecraftRequirement;
import ca.fxco.memoryleakfix.config.VersionRange;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Supplier;

@MinecraftRequirement(@VersionRange(minVersion = "1.14.4"))
@Mixin(Biome.class)
public abstract class Biome_threadLocalMixin {

    /*
     * Not only do I absolutely hate this garbage cache, cause it barely works at all.
     * HOW did nobody at mojang notice that this thread local was not static, how on earth is a non-static private
     * value suppose to run on multiple threads at the same time.
     * So now its static so that it actually works like its suppose to, and you don't need to declare a billion of them
     *
     * This is a significant performance boost & reduces a lot of memory usage
     */

    private static ThreadLocal<Long2FloatLinkedOpenHashMap> memoryLeakFix$betterTempCache;

    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/ThreadLocal;withInitial(Ljava/util/function/Supplier;)Ljava/lang/ThreadLocal;"
            )
    )
    private ThreadLocal<Long2FloatLinkedOpenHashMap> memoryLeakFix$useStaticThreadLocal(Supplier<?> supplier, Operation<ThreadLocal<Long2FloatLinkedOpenHashMap>> original) {
        if (memoryLeakFix$betterTempCache == null) {
            memoryLeakFix$betterTempCache = original.call(supplier);
        }
        return memoryLeakFix$betterTempCache;
    }
}
