package dev.mariany.martweaks.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.mariany.martweaks.block.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.stat.StatType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;
import java.util.function.Function;

@Mixin(StatType.class)
public class StatTypeMixin<K, V> {
    @WrapOperation(method = "getOrCreateStat(Ljava/lang/Object;Lnet/minecraft/stat/StatFormatter;)Lnet/minecraft/stat/Stat;", at = @At(value = "INVOKE", target = "Ljava/util/Map;computeIfAbsent(Ljava/lang/Object;Ljava/util/function/Function;)Ljava/lang/Object;"))
    public V getOrCreateStat(Map<K, V> instance, K key, Function<? super K, ? extends V> compute,
                             Operation<V> original) {
        if (key instanceof Block && key.equals(ModBlocks.SUNFLOWER)) {
            return original.call(instance, Blocks.SUNFLOWER, compute);
        }
        return original.call(instance, key, compute);
    }
}
