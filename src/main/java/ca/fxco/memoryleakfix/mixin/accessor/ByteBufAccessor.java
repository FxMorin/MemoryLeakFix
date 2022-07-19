package ca.fxco.memoryleakfix.mixin.accessor;

import io.netty.buffer.ByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = ByteBuf.class, remap = false)
public interface ByteBufAccessor {
    @Invoker("isAccessible")
    boolean invokeIsAccessible();
}
