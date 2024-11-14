package dev.mariany.martweaks.mixin.accessor;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AdvancementManager.class)
public interface AdvancementManagerAccesor {
    @Invoker("tryAdd")
    boolean martweaks$tryAdd(AdvancementEntry advancement);
}
