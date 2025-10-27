package net.hollowed.combatamenities.util.properties;

import com.mojang.serialization.MapCodec;
import net.hollowed.combatamenities.util.items.CAComponents;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HeldItemContext;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record IntegerComponentProperty() implements NumericProperty {
    public static final MapCodec<IntegerComponentProperty> CODEC = MapCodec.unit(new IntegerComponentProperty());

    @Override
    public float getValue(ItemStack stack, @Nullable ClientWorld world, @Nullable HeldItemContext context, int seed) {
        return stack.get(CAComponents.INTEGER_PROPERTY) != null ? Objects.requireNonNull(stack.get(CAComponents.INTEGER_PROPERTY)) : 0;
    }

    @Override
    public MapCodec<? extends NumericProperty> getCodec() {
        return CODEC;
    }
}
