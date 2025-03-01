package net.hollowed.combatamenities.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public record TransformData(
        Identifier item,
        List<Float> scale,
        List<Float> rotation,
        List<Float> translation,
        ModelTransformationMode mode,
        Float sway,
        Map<String, SubTransformData> componentTransforms // Map of int -> TransformData
) {
    public static final Codec<TransformData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("item").forGetter(TransformData::item),
            Codec.FLOAT.listOf().fieldOf("scale").orElseGet(() -> List.of(1.0f, 1.0f, 1.0f)).forGetter(TransformData::scale),
            Codec.FLOAT.listOf().fieldOf("rotation").orElseGet(() -> List.of(0.0f, 0.0f, 0.0f)).forGetter(TransformData::rotation),
            Codec.FLOAT.listOf().fieldOf("translation").orElseGet(() -> List.of(0.0f, 0.0f, 0.0f)).forGetter(TransformData::translation),
            Codec.STRING.fieldOf("mode").orElse("FIXED")
                    .xmap(ModelTransformationMode::valueOf, ModelTransformationMode::name)
                    .forGetter(TransformData::mode),
            Codec.FLOAT.fieldOf("sway").orElse(1.0F).forGetter(TransformData::sway),
            Codec.unboundedMap(Codec.STRING, SubTransformData.CODEC) // Map<Integer, SubTransformData>
                    .fieldOf("componentTransforms").orElse(Map.of())
                    .forGetter(TransformData::componentTransforms)
    ).apply(instance, TransformData::new));

    // Sub-class to store transformations per component value
    public record SubTransformData(
            List<Float> scale,
            List<Float> rotation,
            List<Float> translation,
            ModelTransformationMode mode,
            Float sway
    ) {
        public static final Codec<SubTransformData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.listOf().fieldOf("scale").orElseGet(() -> List.of(1.0f, 1.0f, 1.0f)).forGetter(SubTransformData::scale),
                Codec.FLOAT.listOf().fieldOf("rotation").orElseGet(() -> List.of(0.0f, 0.0f, 0.0f)).forGetter(SubTransformData::rotation),
                Codec.FLOAT.listOf().fieldOf("translation").orElseGet(() -> List.of(0.0f, 0.0f, 0.0f)).forGetter(SubTransformData::translation),
                Codec.STRING.fieldOf("mode").orElse("FIXED")
                        .xmap(ModelTransformationMode::valueOf, ModelTransformationMode::name)
                        .forGetter(SubTransformData::mode),
                Codec.FLOAT.fieldOf("sway").orElse(1.0F).forGetter(SubTransformData::sway)
        ).apply(instance, SubTransformData::new));
    }
}
