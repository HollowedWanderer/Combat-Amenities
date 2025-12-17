package net.hollowed.combatamenities.util.properties;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.combatamenities.util.items.CAComponents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record BooleanComponentProperty() implements ConditionalItemModelProperty {
	public static final MapCodec<BooleanComponentProperty> CODEC = MapCodec.unit(new BooleanComponentProperty());

	@Override
	public boolean get(ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity user, int seed, @NotNull ItemDisplayContext modelTransformationMode) {
		return Boolean.TRUE.equals(stack.get(CAComponents.BOOLEAN_PROPERTY));
	}

	@Override
	public @NotNull MapCodec<BooleanComponentProperty> type() {
		return CODEC;
	}
}
