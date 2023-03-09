package ca.fxco.memoryleakfix.fabric;

import ca.fxco.memoryleakfix.MemoryLeakFixExpectPlatform;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;

@SuppressWarnings("unused")
public class MemoryLeakFixExpectPlatformImpl {

    /**
     * This is our actual method to {@link MemoryLeakFixExpectPlatform#isModLoaded}.
     */
    public static boolean isModLoaded(String id) {
        return FabricLoader.getInstance().isModLoaded(id);
    }

    /**
     * This is our actual method to {@link MemoryLeakFixExpectPlatform#compareMinecraftToVersion}.
     */
    public static int compareMinecraftToVersion(String version) throws VersionParsingException {
        return FabricLoader.getInstance().getModContainer("minecraft").orElseThrow().getMetadata().getVersion().compareTo(Version.parse(version));
    }
}
