package ca.fxco.memoryleakfix;

import ca.fxco.memoryleakfix.config.mixinExtension.UnMixinExtension;
import ca.fxco.memoryleakfix.utils.MixinInternals;

public class MemoryLeakFixBootstrap {

    private static boolean initialized;

    public static void init() {
        if (!initialized) {
            initialized = true;
            MixinInternals.registerExtension(new UnMixinExtension());
        }
    }
}
