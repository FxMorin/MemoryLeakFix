package ca.fxco.memoryleakfix;

import ca.fxco.memoryleakfix.config.MixinInternals;
import ca.fxco.memoryleakfix.config.mixinExtension.UnMixinExtension;

public class MemoryLeakFixBootstrap {

    public static void init() {
        MixinInternals.registerExtension(new UnMixinExtension());
    }
}
