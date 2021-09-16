package ca.fxco.memoryleakfix.mixin;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRenderer_clientWorldMixin {

    /**
     * Fixes a memory leak in the client where the clientWorld instance is still accessible
     * through ChunkInfo, keeping it alive once the client leaves a world. This ends up being
     * a memory leak since the chunkInfo for that data is never reset and instead just adds
     * more chunkInfo when needed.
     * Keeping the clientWorld instance also keeps all the chunks & world renderer xD
     */

    @Shadow private WorldRenderer.ChunkInfoList chunkInfos;

    @Inject(
            method= "setWorld(Lnet/minecraft/client/world/ClientWorld;)V",
            at=@At(
                    value="INVOKE",
                    target="Ljava/util/Set;clear()V",
                    ordinal=0
            ))
    public void onSetWorld(ClientWorld world, CallbackInfo ci) {
        chunkInfos = null;
    }
}
