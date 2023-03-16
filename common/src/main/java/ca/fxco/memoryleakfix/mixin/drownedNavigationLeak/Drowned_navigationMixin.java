package ca.fxco.memoryleakfix.mixin.drownedNavigationLeak;

import ca.fxco.memoryleakfix.config.MinecraftRequirement;
import ca.fxco.memoryleakfix.config.VersionRange;
import ca.fxco.memoryleakfix.extensions.ExtendDrowned;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@MinecraftRequirement(@VersionRange(minVersion = "1.16.3", maxVersion = "1.16.5"))
@Mixin(Drowned.class)
public abstract class Drowned_navigationMixin extends Zombie implements ExtendDrowned {

    @Shadow @Final protected WaterBoundPathNavigation waterNavigation;

    @Shadow @Final protected GroundPathNavigation groundNavigation;

    private PathNavigation memoryLeakFix$originalPathNavigation;

    public Drowned_navigationMixin(Level level) {
        super(level);
    }

    @Override
    public void memoryLeakFix$onRemoveNavigation(Set<PathNavigation> navigations) {
        navigations.remove(this.memoryLeakFix$originalPathNavigation);
        navigations.remove(this.waterNavigation);
        navigations.remove(this.groundNavigation);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void getOriginalNavigation(CallbackInfo ci) {
        this.memoryLeakFix$originalPathNavigation = this.getNavigation();
    }
}
