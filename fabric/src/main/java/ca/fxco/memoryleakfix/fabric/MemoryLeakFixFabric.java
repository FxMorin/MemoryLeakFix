package ca.fxco.memoryleakfix.fabric;

import ca.fxco.memoryleakfix.MemoryLeakFix;
import net.fabricmc.api.ModInitializer;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MemoryLeakFixFabric implements ModInitializer {

    public static final Set<FriendlyByteBuf> BUFFERS_TO_CLEAR = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void onInitialize() {
        MemoryLeakFix.init();
    }
}
