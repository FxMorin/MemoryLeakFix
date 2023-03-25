package ca.fxco.memoryleakfix.mixin.tagKeyLeak;

import ca.fxco.memoryleakfix.config.MinecraftRequirement;
import org.spongepowered.asm.mixin.Mixin;

// This mixin only gets used to replace the original one in the 1.16.5 build, otherwise it just gets ignored by the build script.
@SuppressWarnings("all")
@MinecraftRequirement({})
@Mixin(Object.class) // targetting object because you have to declare a target and TagKey doesn't exist
public abstract class TagKey_internerMixin {
}
