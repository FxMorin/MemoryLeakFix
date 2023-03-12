package ca.fxco.memoryleakfix.forge;

import ca.fxco.memoryleakfix.MemoryLeakFixExpectPlatform;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

@SuppressWarnings("unused")
public class MemoryLeakFixExpectPlatformImpl {

    private static final ArtifactVersion MCVERSION;

    static {
        String mcVersion;
        try {
            // new forge
            Object versionInfo = FMLLoader.class.getMethod("versionInfo").invoke(null);
            mcVersion = (String) versionInfo.getClass().getMethod("mcVersion").invoke(versionInfo);
        } catch (Exception e) {
            try {
                // old forge
                mcVersion = (String) FMLLoader.class.getField("mcVersion").get(null);
            } catch (Exception ex) {
                throw new RuntimeException("[MemoryLeakFix] Reflection failed at getting the Minecraft version from Forge", ex);
            }
        }
        MCVERSION = new DefaultArtifactVersion(mcVersion);
    }

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
        return MCVERSION.compareTo(new DefaultArtifactVersion(version));
    }
}
