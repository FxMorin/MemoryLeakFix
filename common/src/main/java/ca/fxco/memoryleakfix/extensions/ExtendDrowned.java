package ca.fxco.memoryleakfix.extensions;

import net.minecraft.world.entity.ai.navigation.PathNavigation;

import java.util.Set;

// Only available from 1.16.3 to 1.16.5
public interface ExtendDrowned {

    void memoryLeakFix$onRemoveNavigation(Set<PathNavigation> navigations);
}
