package ca.fxco.memoryleakfix.forge;

import ca.fxco.memoryleakfix.MemoryLeakFixExpectPlatform;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.lang.reflect.Field;

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
                Field field = FMLLoader.class.getDeclaredField("mcVersion");
                field.setAccessible(true);
                mcVersion = (String) field.get(null);
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

    /**
     * This is our actual method to {@link MemoryLeakFixExpectPlatform#getMappingType}.
     */
    public static String getMappingType() {
        return MCVERSION.compareTo(new DefaultArtifactVersion("1.16.5")) > 0 ? "forge" : "mcp";
    }

    /**
     * This is our actual method to {@link MemoryLeakFixExpectPlatform#isDevEnvironment}.
     */
    public static boolean isDevEnvironment() {
        return !FMLLoader.isProduction();
    }
}
