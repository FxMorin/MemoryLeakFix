package ca.fxco.memoryleakfix.fabric;

import ca.fxco.memoryleakfix.MemoryLeakFix;
import ca.fxco.memoryleakfix.MemoryLeakFixExpectPlatform;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.network.FriendlyByteBuf;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.logging.LoggerAdapterDefault;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.injection.struct.InjectorGroupInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;

import java.lang.reflect.Field;
import java.util.*;

public class MemoryLeakFixFabric implements ModInitializer {

    public static final Set<FriendlyByteBuf> BUFFERS_TO_CLEAR = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void onInitialize() {
        MemoryLeakFix.init();
    }

    public static void forceLoadAllMixinsAndClearSpongePoweredCache() {
        try {
            internalForceLoadAllMixinsAndClearSpongePoweredCache();
        } catch (VersionParsingException ignored) {}
    }

    private static void internalForceLoadAllMixinsAndClearSpongePoweredCache() throws VersionParsingException {
        // Must be fabric loader version smaller than v0.14.14. Patched in v0.14.15
        if (Version.parse(FabricLoaderImpl.VERSION).compareTo(Version.parse("0.14.15")) < 0) {
            MemoryLeakFix.LOGGER.info("[MemoryLeakFix] Attempting to ForceLoad All Mixins and clear cache");
            silenceAuditLogger();
            MixinEnvironment.getCurrentEnvironment().audit();
            try {
                Field noGroupField = InjectorGroupInfo.Map.class.getDeclaredField("NO_GROUP");
                noGroupField.setAccessible(true);
                Object noGroup = noGroupField.get(null);
                Field membersField = noGroup.getClass().getDeclaredField("members");
                membersField.setAccessible(true);
                ((List<?>) membersField.get(noGroup)).clear(); // Clear spongePoweredCache
                emptyClassInfo();
            } catch (NoSuchFieldException | IllegalAccessException ignored) {}
            MemoryLeakFix.LOGGER.info("[MemoryLeakFix] Done ForceLoad and clearing SpongePowered cache");
        }
    }

    private static Class<?> getMixinLoggerClass() throws ClassNotFoundException {
        Class<?> mixinLogger;
        try {
            mixinLogger = Class.forName("net.fabricmc.loader.impl.launch.knot.MixinLogger");
        } catch (ClassNotFoundException err) {
            mixinLogger = Class.forName("org.quiltmc.loader.impl.launch.knot.MixinLogger");
        }
        return mixinLogger;
    }

    private static void silenceAuditLogger() {
        try {
            Field loggerField = getMixinLoggerClass().getDeclaredField("LOGGER_MAP");
            loggerField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, ILogger> loggerMap = (Map<String, ILogger>)loggerField.get(null);
            loggerMap.put("mixin.audit", new LoggerAdapterDefault("mixin.audit"));
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    private static final String OBJECT = "java/lang/Object";

    private static void emptyClassInfo() throws NoSuchFieldException, IllegalAccessException {
        if (MemoryLeakFixExpectPlatform.isModLoaded("not-that-cc"))
            return; // Crashes crafty crashes if it crashes
        Field cacheField = ClassInfo.class.getDeclaredField("cache");
        cacheField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, ClassInfo> cache = ((Map<String, ClassInfo>)cacheField.get(null));
        ClassInfo jlo = cache.get(OBJECT);
        cache.clear();
        cache.put(OBJECT, jlo);
    }
}
