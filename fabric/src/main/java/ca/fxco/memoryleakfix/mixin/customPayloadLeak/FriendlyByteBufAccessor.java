package ca.fxco.memoryleakfix.mixin.customPayloadLeak;

import io.netty.buffer.ByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Environment(EnvType.CLIENT)
@Mixin(FriendlyByteBuf.class)
public interface FriendlyByteBufAccessor {

    @Accessor
    ByteBuf getSource();
}
