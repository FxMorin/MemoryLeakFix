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
    @SuppressWarnings("OptionalGetWithoutIsPresent") // we use Optional#get over Optional#orElseThrow for java 8 compatibility, if minecraft isn't present we've got bigger issues
    public static int compareMinecraftToVersion(String version) throws VersionParsingException {
        return FabricLoader.getInstance().getModContainer("minecraft").get().getMetadata().getVersion().compareTo(Version.parse(version));
    }
}
