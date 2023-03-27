package ca.fxco.memoryleakfix.mixin.entityMemoriesLeak;

import ca.fxco.memoryleakfix.config.MinecraftRequirement;
import ca.fxco.memoryleakfix.config.VersionRange;
import ca.fxco.memoryleakfix.extensions.ExtendBrain;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Optional;

@MinecraftRequirement(@VersionRange(maxVersion = "1.19.3"))
@Mixin(Brain.class)
public abstract class Brain_clearMemoriesMixin implements ExtendBrain {

    @Shadow @Final private Map<MemoryModuleType<?>, Optional<?>> memories;

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType") // mojank ¯\_ (ツ)_/¯
    @Shadow public abstract void setMemory(MemoryModuleType<?> memoryModuleType, Optional<?> optional);

    @Override
    public void memoryLeakFix$clearMemories() {
        this.memories.keySet().forEach(module -> this.setMemory(module, Optional.empty()));
    }
}