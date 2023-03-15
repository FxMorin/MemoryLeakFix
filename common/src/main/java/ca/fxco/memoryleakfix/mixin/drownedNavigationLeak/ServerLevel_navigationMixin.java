package ca.fxco.memoryleakfix.mixin.drownedNavigationLeak;

import ca.fxco.memoryleakfix.config.MinecraftRequirement;
import ca.fxco.memoryleakfix.config.VersionRange;
import ca.fxco.memoryleakfix.extensions.ExtendDrowned;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

/**
 * This fixes: <a href="https://bugs.mojang.com/browse/MC-202246">MC-202246</a>
 */
@MinecraftRequirement(@VersionRange(minVersion = "1.16.3", maxVersion = "1.16.5"))
@Environment(EnvType.SERVER)
@Mixin(ServerLevel.class)
public abstract class ServerLevel_navigationMixin {

    @SuppressWarnings("MixinAnnotationTarget")
    @Shadow
    @Final
    private Set<PathNavigation> navigations;

    @SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference"})
    @Inject(method = "onEntityRemoved", at = @At("RETURN"))
    private void removeAllPossibleDrownedNavigations(Entity entity, CallbackInfo ci) {
        if (entity instanceof ExtendDrowned) {
            ((ExtendDrowned) entity).memoryLeakFix$onRemoveNavigation(navigations);
        }
    }
}
