package ca.fxco.memoryleakfix.mixin.customPayloadLeak;

import ca.fxco.memoryleakfix.extensions.ExtendPacketByteBuf;
import ca.fxco.memoryleakfix.mixin.accessor.AbstractReferenceCountedByteBufAccessor;
import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PacketByteBuf.class)
public class PacketByteBuf_extendMixin implements ExtendPacketByteBuf {

    @Shadow
    @Final
    private ByteBuf parent;


    @Override
    public boolean isAccessible() {
        if (this.parent instanceof AbstractReferenceCountedByteBuf arcbb)
            return ((AbstractReferenceCountedByteBufAccessor)arcbb).invokeIsAccessible();
        return this.parent.refCnt() != 0;
    }
}
