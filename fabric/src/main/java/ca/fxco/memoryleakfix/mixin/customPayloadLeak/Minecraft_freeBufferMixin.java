package ca.fxco.memoryleakfix.mixin.customPayloadLeak;

import ca.fxco.memoryleakfix.fabric.MemoryLeakFixFabric;
import io.netty.buffer.AbstractReferenceCountedByteBuf;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public abstract class Minecraft_freeBufferMixin {

    /*
     * Free the packets at the end of the tick
     */

    // Make sure that there is a reference to release first!
    private boolean memoryLeakFix$tryRelease(FriendlyByteBuf buffer) {
        if (!(((FriendlyByteBufAccessor) buffer).getSource() instanceof AbstractReferenceCountedByteBuf)) {
            return buffer.refCnt() == 0 && buffer.release();
        }
        return true;
    }


    @Inject(
            method = "tick",
            at = @At("RETURN")
    )
    private void memoryLeakFix$releaseBuffersAfterTick(CallbackInfo ci) {
        MemoryLeakFixFabric.BUFFERS_TO_CLEAR.removeIf(this::memoryLeakFix$tryRelease);
    }
}
