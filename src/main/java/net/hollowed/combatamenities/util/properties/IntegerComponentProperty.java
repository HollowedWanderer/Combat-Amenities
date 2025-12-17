package net.hollowed.combatamenities.util.properties;

import com.mojang.serialization.MapCodec;
import net.hollowed.combatamenities.util.items.CAComponents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record IntegerComponentProperty() implements RangeSelectItemModelProperty {
    public static final MapCodec<IntegerComponentProperty> CODEC = MapCodec.unit(new IntegerComponentProperty());

    @Override
    public float get(ItemStack stack, @Nullable ClientLevel world, @Nullable ItemOwner context, int seed) {
        return stack.get(CAComponents.INTEGER_PROPERTY) != null ? Objects.requireNonNull(stack.get(CAComponents.INTEGER_PROPERTY)) : 0;
    }

    @Override
    public @NotNull MapCodec<? extends RangeSelectItemModelProperty> type() {
        return CODEC;
    }
}
