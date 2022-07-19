package ca.fxco.memoryleakfix.mixin.accessor;

import io.netty.buffer.ByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ByteBuf.class)
public interface ByteBufAccessor {
    @Invoker("isAccessible")
    boolean invokeIsAccessible();
}
