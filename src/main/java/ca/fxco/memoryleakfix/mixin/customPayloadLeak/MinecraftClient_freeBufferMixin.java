package ca.fxco.memoryleakfix.mixin.customPayloadLeak;

import ca.fxco.memoryleakfix.MemoryLeakFix;
import ca.fxco.memoryleakfix.extensions.ExtendPacketByteBuf;
import io.netty.buffer.AbstractReferenceCountedByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public class MinecraftClient_freeBufferMixin {

    /*
     * Free the packets at the end of the tick
     */

    // Make sure that there is a reference to release first!
    private boolean tryRelease(PacketByteBuf buf) {
        if (buf instanceof ExtendPacketByteBuf extBuf &&
                !(extBuf.getParent() instanceof AbstractReferenceCountedByteBuf)) {
            return buf.refCnt() == 0 && buf.release();
        }
        return true;
    }


    @Inject(
            method = "tick",
            at = @At("RETURN")
    )
    private void releaseAfterTick(CallbackInfo ci) {
        MemoryLeakFix.BUFFERS_TO_CLEAR.removeIf(this::tryRelease);
    }
}
