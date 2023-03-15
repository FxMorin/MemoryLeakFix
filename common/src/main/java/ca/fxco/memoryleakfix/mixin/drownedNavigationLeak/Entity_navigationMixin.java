package ca.fxco.memoryleakfix.mixin.drownedNavigationLeak;

import ca.fxco.memoryleakfix.config.MinecraftRequirement;
import ca.fxco.memoryleakfix.config.VersionRange;
import ca.fxco.memoryleakfix.extensions.ExtendEntity;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@MinecraftRequirement(@VersionRange(minVersion = "1.16.3", maxVersion = "1.16.5"))
@Mixin(Entity.class)
public class Entity_navigationMixin implements ExtendEntity {}
