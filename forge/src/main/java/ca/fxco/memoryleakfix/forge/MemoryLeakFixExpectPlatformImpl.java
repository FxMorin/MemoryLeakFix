package ca.fxco.memoryleakfix.forge;

import ca.fxco.memoryleakfix.MemoryLeakFixExpectPlatform;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

@SuppressWarnings("unused")
public class MemoryLeakFixExpectPlatformImpl {

    /**
     * This is our actual method to {@link MemoryLeakFixExpectPlatform#isModLoaded}.
     */
    public static boolean isModLoaded(String id) {
        return ModList.get().isLoaded(id);
    }

    /**
     * This is our actual method to {@link MemoryLeakFixExpectPlatform#compareMinecraftToVersion}.
     */
    public static int compareMinecraftToVersion(String version) {
        return new DefaultArtifactVersion(FMLLoader.versionInfo().mcVersion()).compareTo(new DefaultArtifactVersion(version));
    }
}
