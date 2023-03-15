package ca.fxco.memoryleakfix.mixin.hugeScreenshotLeak;

import ca.fxco.memoryleakfix.config.MinecraftRequirement;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

// This mixin only gets used to replace the original one in the 1.16.5 build, otherwise it just gets ignored by the build script.
@MinecraftRequirement({})
@Mixin(Minecraft.class)
public abstract class Minecraft_screenshotMixin {
}
