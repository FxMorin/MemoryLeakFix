package ca.fxco.memoryleakfix.mixin.customPayloadLeak;

import ca.fxco.memoryleakfix.extensions.ExtendPacketByteBuf;
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
    public ByteBuf getParent() {
        return this.parent;
    }
}