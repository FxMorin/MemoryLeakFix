package ca.fxco.memoryleakfix.mixin.entityMemoriesLeak;

import ca.fxco.memoryleakfix.config.MinecraftRequirement;
import ca.fxco.memoryleakfix.extensions.ExtendBrain;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Optional;

@MinecraftRequirement(maxVersion = "1.19.3")
@Mixin(Brain.class)
public abstract class Brain_clearMemoriesMixin implements ExtendBrain {

    @Shadow @Final private Map<MemoryModuleType<?>, Optional<?>> memories;

    @Override
    public void memoryLeakFix$clearMemories() {
        this.memories.keySet().forEach(module -> this.memories.put(module, Optional.empty()));
    }
}