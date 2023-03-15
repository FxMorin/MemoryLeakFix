package ca.fxco.memoryleakfix;

import ca.fxco.memoryleakfix.config.MixinInternals;
import ca.fxco.memoryleakfix.config.mixinExtension.UnMixinExtension;

public class MemoryLeakFixBootstrap {

    private static boolean initialized;

    public static void init() {
        if (!initialized) {
            initialized = true;
            MixinInternals.registerExtension(new UnMixinExtension());
        }
    }
}
