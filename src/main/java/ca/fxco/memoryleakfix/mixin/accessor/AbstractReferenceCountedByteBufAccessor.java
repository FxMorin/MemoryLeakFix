package ca.fxco.memoryleakfix.mixin.accessor;

import io.netty.buffer.AbstractReferenceCountedByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = AbstractReferenceCountedByteBuf.class, remap = false)
public interface AbstractReferenceCountedByteBufAccessor {
    @Invoker("isAccessible")
    boolean invokeIsAccessible();
}
