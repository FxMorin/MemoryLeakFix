package ca.fxco.memoryleakfix;

import ca.fxco.memoryleakfix.config.mixinExtension.UnMixinExtension;
import com.llamalad7.mixinextras.utils.MixinInternals;

public class MemoryLeakFixBootstrap {

    private static boolean initialized;

    public static void init() {
        if (!initialized) {
            initialized = true;
            MixinInternals.registerExtension(new UnMixinExtension());
        }
    }
}
