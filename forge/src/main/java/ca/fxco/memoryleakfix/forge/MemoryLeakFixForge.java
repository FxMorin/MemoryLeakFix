package ca.fxco.memoryleakfix.forge;

import ca.fxco.memoryleakfix.MemoryLeakFix;
import net.minecraftforge.fml.common.Mod;

@Mod(MemoryLeakFix.MOD_ID)
public class MemoryLeakFixForge {

    public MemoryLeakFixForge() {
        MemoryLeakFix.init();
    }
}
