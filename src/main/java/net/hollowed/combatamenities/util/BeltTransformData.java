package net.hollowed.combatamenities.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.util.Identifier;

import java.util.List;

public record BeltTransformData(
        Identifier item,
        List<Float> scale,        // List for scale
        List<Float> rotation,     // List for rotation
        List<Float> translation,  // List for translation
        ModelTransformationMode mode,    // Add the TransformationMode field
        Float sway
) {
    public static final Codec<BeltTransformData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("item").forGetter(BeltTransformData::item),
            Codec.FLOAT.listOf().fieldOf("scale").orElseGet(() -> List.of(1.0f, 1.0f, 1.0f)).forGetter(BeltTransformData::scale),
            Codec.FLOAT.listOf().fieldOf("rotation").orElseGet(() -> List.of(0.0f, 0.0f, 0.0f)).forGetter(BeltTransformData::rotation),
            Codec.FLOAT.listOf().fieldOf("translation").orElseGet(() -> List.of(0.0f, 0.0f, 0.0f)).forGetter(BeltTransformData::translation),
            Codec.STRING.fieldOf("mode").orElse("GUI") // Default to GUI if no mode is provided
                    .xmap(ModelTransformationMode::valueOf, ModelTransformationMode::name)
                    .forGetter(BeltTransformData::mode),
            Codec.FLOAT.fieldOf("sway").orElse(1.0F).forGetter(BeltTransformData::sway)
    ).apply(instance, BeltTransformData::new));
}
