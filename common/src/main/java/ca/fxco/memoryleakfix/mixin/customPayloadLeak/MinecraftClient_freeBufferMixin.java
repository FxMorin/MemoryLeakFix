package ca.fxco.memoryleakfix.mixin.customPayloadLeak;

import ca.fxco.memoryleakfix.MemoryLeakFix;
import io.netty.buffer.AbstractReferenceCountedByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(MinecraftClient.class)
public abstract class MinecraftClient_freeBufferMixin {

    /*
     * Free the packets at the end of the tick
     */

    // Make sure that the there is a reference to release first!
    @Unique
    private boolean memoryLeakFix$tryRelease(PacketByteBuf buffer) {
        if (!(((PacketByteBufAccessor) buffer).getParent() instanceof AbstractReferenceCountedByteBuf)) {
            return buffer.refCnt() == 0 && buffer.release();
        }
        return true;
    }


    @Inject(
            method = "tick",
            at = @At("RETURN")
    )
    private void memoryLeakFix$releaseBuffersAfterTick(CallbackInfo ci) {
        MemoryLeakFix.BUFFERS_TO_CLEAR.removeIf(this::memoryLeakFix$tryRelease);
    }
}
