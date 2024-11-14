package dev.mariany.martweaks.mixin.accessor;

import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.advancement.criterion.CriterionProgress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(AdvancementProgress.class)
public interface AdvancementProgressAccessor {
    @Accessor("criteriaProgresses")
    Map<String, CriterionProgress> martweaks$criteriaProgresses();
}
