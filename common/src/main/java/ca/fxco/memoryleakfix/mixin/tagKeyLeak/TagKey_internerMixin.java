package ca.fxco.memoryleakfix.mixin.tagKeyLeak;

import ca.fxco.memoryleakfix.config.MinecraftRequirement;
import ca.fxco.memoryleakfix.config.VersionRange;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import net.minecraft.tags.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * This fixes: <a href="https://bugs.mojang.com/browse/MC-248621">MC-248621</a>
 */
@MinecraftRequirement(@VersionRange(minVersion = "1.18.2", maxVersion = "1.18.2"))
@Mixin(value = TagKey.class, priority = 1500)
public abstract class TagKey_internerMixin {

    @SuppressWarnings({"MixinAnnotationTarget", "UnresolvedMixinReference", "InvalidInjectorMethodSignature"})
    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/Interners;newStrongInterner()" +
                            "Lcom/google/common/collect/Interner;"
            )
    )
    private static Interner<TagKey<?>> memoryLeakFix$useWeakInterner() {
        return Interners.newWeakInterner();
    }
}
