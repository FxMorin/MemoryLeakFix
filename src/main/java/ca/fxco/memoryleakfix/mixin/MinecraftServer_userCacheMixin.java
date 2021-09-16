package ca.fxco.memoryleakfix.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.UserCache;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServer_userCacheMixin {

    /**
     * Fixes userCache executor keeping the last minecraftServer instance in memory instead
     * of unloading it once the server closes. Mostly due to SkullBlockEntity
     */

    @Shadow @Final @Nullable private UserCache userCache;

    @Inject(method= "shutdown()V",at=@At("RETURN"))
    public void onShutdown(CallbackInfo ci) {
        userCache.setExecutor(null);
    }
}
