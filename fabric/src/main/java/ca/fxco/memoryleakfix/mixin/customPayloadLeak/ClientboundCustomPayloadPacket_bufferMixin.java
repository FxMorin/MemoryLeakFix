package ca.fxco.memoryleakfix.mixin.customPayloadLeak;

import ca.fxco.memoryleakfix.config.MinecraftRequirement;
import ca.fxco.memoryleakfix.config.VersionRange;
import ca.fxco.memoryleakfix.fabric.MemoryLeakFixFabric;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@MinecraftRequirement(@VersionRange(maxVersion = "1.20.1"))
@Environment(EnvType.CLIENT)
@Mixin(targets = "net/minecraft/class_2658", remap = false)
public abstract class ClientboundCustomPayloadPacket_bufferMixin {

    /*
     * The issue here is that for Custom Payload packets, the netty buffer is never freed.
     * Unlike the bug report states [MC-121884](https://bugs.mojang.com/browse/MC-121884)
     * You cannot simply release the packet once it's been applied, since packets are sometimes used between
     * multiple apply calls, this can lead to multiple issues.
     *
     * Therefore, what we do is store them (they are not going to unload anyway) and then free them later in the tick.
     *
     * By Fx Morin
     */

    /*
     * Forge fixes this memory leak, so we put it fabric-side.
     */


    @Shadow
    @Final
    private FriendlyByteBuf data;


    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(
            method = "handle(Lnet/minecraft/network/protocol/game/ClientGamePacketListener;)V",
            at = @At("RETURN")
    )
    private void memoryLeakFix$storeBufferToClear(CallbackInfo ci) {
        MemoryLeakFixFabric.BUFFERS_TO_CLEAR.add(this.data);
    }
}
