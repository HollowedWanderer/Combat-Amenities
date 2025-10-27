package net.hollowed.combatamenities.util.properties;

import com.mojang.serialization.MapCodec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.combatamenities.util.items.CAComponents;
import net.minecraft.client.render.item.property.bool.BooleanProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public record BooleanComponentProperty() implements BooleanProperty {
	public static final MapCodec<BooleanComponentProperty> CODEC = MapCodec.unit(new BooleanComponentProperty());

	@Override
	public boolean test(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity user, int seed, ItemDisplayContext modelTransformationMode) {
		return Boolean.TRUE.equals(stack.get(CAComponents.BOOLEAN_PROPERTY));
	}

	@Override
	public MapCodec<BooleanComponentProperty> getCodec() {
		return CODEC;
	}
}
