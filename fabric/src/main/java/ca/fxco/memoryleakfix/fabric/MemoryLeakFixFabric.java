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
}
