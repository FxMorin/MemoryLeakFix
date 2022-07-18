package ca.fxco.memoryleakfix.mixin.customPayloadLeak;

import ca.fxco.memoryleakfix.MemoryLeakFix;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CustomPayloadS2CPacket.class)
public class CustomPayloadS2CPacket_bufferMixin {

    /*
     * The issue here is that for Custom Payload packets, the netty buffer is never freed.
     * Unlike the bug report states [MC-121884](https://bugs.mojang.com/browse/MC-121884)
     * You cannot simply release the packet once it's been applied, since packets are sometimes used between
     * multiple apply calls,this can lead to multiple issues.
     *
     * Therefore, what we do is store them (they are not going to unload anyway) and then free them later in the tick.
     *
     * By Fx Morin
     */


    @Shadow
    @Final
    private PacketByteBuf data;


    @Inject(
            method = "apply(Lnet/minecraft/network/listener/ClientPlayPacketListener;)V",
            at = @At("RETURN")
    )
    private void afterApply(ClientPlayPacketListener clientPlayPacketListener, CallbackInfo ci) {
        MemoryLeakFix.BUFFERS_TO_CLEAR.add(this.data);
    }
}
