package net.hollowed.combatamenities.mixin.items.properties;

import com.mojang.serialization.MapCodec;
import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.util.properties.IntegerComponentProperty;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RangeSelectItemModelProperties.class)
public class NumericPropertyMixin {
    @Shadow @Final public static ExtraCodecs.LateBoundIdMapper<Identifier, MapCodec<? extends RangeSelectItemModelProperty>> ID_MAPPER;

    @Inject(method = "bootstrap", at = @At("HEAD"))
    private static void bootstrap(CallbackInfo ci) {
        ID_MAPPER.put(Identifier.fromNamespaceAndPath(CombatAmenities.MOD_ID, "int_property"), IntegerComponentProperty.CODEC);
    }
}
