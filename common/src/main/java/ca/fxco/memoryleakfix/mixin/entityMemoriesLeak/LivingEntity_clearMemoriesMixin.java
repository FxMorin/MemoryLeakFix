package ca.fxco.memoryleakfix.mixin.entityMemoriesLeak;

import ca.fxco.memoryleakfix.config.MinecraftRequirement;
import ca.fxco.memoryleakfix.extensions.ExtendBrain;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/*
 * Fixes https://bugs.mojang.com/browse/MC-260605
 * This was fixed by mojang in 1.19.4
 */

@MinecraftRequirement(maxVersion = "1.19.3")
@Mixin(LivingEntity.class)
public abstract class LivingEntity_clearMemoriesMixin extends Entity_clearMemoriesMixin {

    @Shadow protected Brain<?> brain;

    @Override
    protected void memoryLeakFix$OnEntityRemoved(CallbackInfo ci) {
        ((ExtendBrain)this.brain).memoryLeakFix$clearMemories();
    }
}