package ca.fxco.memoryleakfix.fabric;

import ca.fxco.memoryleakfix.MemoryLeakFix;
import net.fabricmc.api.ModInitializer;

public class MemoryLeakFixFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        MemoryLeakFix.init();
    }
}
