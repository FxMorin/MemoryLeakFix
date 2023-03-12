package ca.fxco.memoryleakfix;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.logging.LoggerAdapterDefault;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.injection.struct.InjectorGroupInfo;
import org.spongepowered.asm.mixin.transformer.ClassInfo;

import java.lang.reflect.Field;
import java.util.*;

public class MemoryLeakFix {

    public static final String MOD_ID = "memoryleakfix";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static void init() {}
}
