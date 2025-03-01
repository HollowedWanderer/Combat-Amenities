package net.hollowed.combatamenities.util.properties;

import com.mojang.serialization.MapCodec;
import net.hollowed.combatamenities.util.ModComponents;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record IntegerComponentProperty() implements NumericProperty {
    public static final MapCodec<IntegerComponentProperty> CODEC = MapCodec.unit(new IntegerComponentProperty());

    @Override
    public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity holder, int seed) {
        return stack.get(ModComponents.INTEGER_PROPERTY) != null ? Objects.requireNonNull(stack.get(ModComponents.INTEGER_PROPERTY)) : 0;
    }

    @Override
    public MapCodec<? extends NumericProperty> getCodec() {
        return CODEC;
    }
}
