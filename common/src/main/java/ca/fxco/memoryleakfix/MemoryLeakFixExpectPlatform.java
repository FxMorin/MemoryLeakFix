package ca.fxco.memoryleakfix;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class MemoryLeakFixExpectPlatform {

    @ExpectPlatform
    public static boolean isModLoaded(String id) {
        // Just throw an error, the content should get replaced at runtime.
        throw new AssertionError();
    }

    @ExpectPlatform
    public static int compareMinecraftToVersion(String version) {
        throw new AssertionError();
    }
}
