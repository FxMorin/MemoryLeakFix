package ca.fxco.memoryleakfix;

import net.fabricmc.api.ModInitializer;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.injection.struct.InjectorGroupInfo;

import java.lang.reflect.Field;
import java.util.List;

public class memoryLeakFix implements ModInitializer {
    @Override
    public void onInitialize() {}

    public static void forceLoadAllMixinsAndClearSpongePoweredCache() {
        MixinEnvironment.getCurrentEnvironment().audit();
        try { //Why is SpongePowered stealing so much ram for this garbage?
            Field noGroupField = InjectorGroupInfo.Map.class.getDeclaredField("NO_GROUP");
            noGroupField.setAccessible(true);
            Object noGroup = noGroupField.get(null);
            Field membersField = noGroup.getClass().getDeclaredField("members");
            membersField.setAccessible(true);
            ((List<?>) membersField.get(noGroup)).clear(); // Clear spongePoweredCache
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
