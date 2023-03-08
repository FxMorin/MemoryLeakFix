package ca.fxco.memoryleakfix.mixin.entityMemoriesLeak;

import ca.fxco.memoryleakfix.extensions.ExtendBrain;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/*
 * Fixes https://bugs.mojang.com/browse/MC-260605
 * This was fixed by mojang in 1.19.4
 */

// TODO: Needs to be removed in 1.19.4+
@Mixin(LivingEntity.class)
public abstract class LivingEntity_clearMemoriesMixin extends Entity {

    @Shadow protected Brain<?> brain;

    public LivingEntity_clearMemoriesMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    public void remove(Entity.RemovalReason reason) {
        super.remove(reason);
        ((ExtendBrain)this.brain).clearMemories();
    }

}