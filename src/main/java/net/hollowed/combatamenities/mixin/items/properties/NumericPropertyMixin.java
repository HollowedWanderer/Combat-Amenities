package net.hollowed.combatamenities.mixin.items.properties;

import com.mojang.serialization.MapCodec;
import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.util.properties.IntegerComponentProperty;
import net.minecraft.client.render.item.property.numeric.NumericProperties;
import net.minecraft.client.render.item.property.numeric.NumericProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NumericProperties.class)
public class NumericPropertyMixin {
    @Shadow @Final public static Codecs.IdMapper<Identifier, MapCodec<? extends NumericProperty>> ID_MAPPER;

    @Inject(method = "bootstrap", at = @At("HEAD"))
    private static void bootstrap(CallbackInfo ci) {
        System.out.println("Registered new properties");
        ID_MAPPER.put(Identifier.of(CombatAmenities.MOD_ID, "int_property"), IntegerComponentProperty.CODEC);
    }
}
