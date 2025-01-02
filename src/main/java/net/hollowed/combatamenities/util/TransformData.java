package net.hollowed.combatamenities.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Identifier;

import java.util.List;

public record TransformData(
        Identifier item,
        List<Float> scale,        // List for scale
        List<Float> rotation,     // List for rotation
        List<Float> translation,  // List for translation
        ModelTransformationMode mode,    // Add the TransformationMode field
        Float sway
) {
    public static final Codec<TransformData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("item").forGetter(TransformData::item),
            Codec.FLOAT.listOf().fieldOf("scale").orElseGet(() -> List.of(1.0f, 1.0f, 1.0f)).forGetter(TransformData::scale),
            Codec.FLOAT.listOf().fieldOf("rotation").orElseGet(() -> List.of(0.0f, 0.0f, 0.0f)).forGetter(TransformData::rotation),
            Codec.FLOAT.listOf().fieldOf("translation").orElseGet(() -> List.of(0.0f, 0.0f, 0.0f)).forGetter(TransformData::translation),
            Codec.STRING.fieldOf("mode").orElse("FIXED") // Default to FIXED if no mode is provided
                    .xmap(ModelTransformationMode::valueOf, ModelTransformationMode::name)
                    .forGetter(TransformData::mode),
            Codec.FLOAT.fieldOf("sway").orElse(1.0F).forGetter(TransformData::sway)
    ).apply(instance, TransformData::new));
}
