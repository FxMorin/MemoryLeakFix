package ca.fxco.memoryleakfix.mixin.biomeTemperatureLeak;

import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import net.minecraft.util.Util;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Supplier;

@Mixin(Biome.class)
public class Biome_threadLocalMixin {

    /*
     * Not only do I absolutely hate this garbage cache, cause it barely works at all.
     * HOW did nobody at mojang notice that this thread local was not static, how on earth is a non-static private
     * value suppose to run on multiple threads at the same time.
     * So now its static so that it actually works like its suppose to, and you don't need to declare a billion of them
     *
     * This is a significant performance boost & reduces a lot of memory usage
     */

    private static final ThreadLocal<Long2FloatLinkedOpenHashMap> betterTempCache = ThreadLocal.withInitial(() ->
            Util.make(() -> {
                Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = new Long2FloatLinkedOpenHashMap(
                        1024, 0.25F
                ) {
                    @Override protected void rehash(int n) {}
                };
                long2FloatLinkedOpenHashMap.defaultReturnValue(Float.NaN);
                return long2FloatLinkedOpenHashMap;
            })
    );


    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/ThreadLocal;withInitial(Ljava/util/function/Supplier;)Ljava/lang/ThreadLocal;"
            )
    )
    private ThreadLocal<Long2FloatLinkedOpenHashMap> useStaticThreadLocal(Supplier<?> supplier) {
        return betterTempCache;
    }
}
