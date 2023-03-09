package ca.fxco.memoryleakfix.mixin.customPayloadLeak;

import io.netty.buffer.ByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(PacketByteBuf.class)
public interface PacketByteBufAccessor {

    @Accessor
    ByteBuf getParent();
}
