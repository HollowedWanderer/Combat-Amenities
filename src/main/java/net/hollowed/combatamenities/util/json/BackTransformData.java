package net.hollowed.combatamenities.util.json;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public record BackTransformData(
        Identifier item,
        List<Float> scale,
        List<Float> rotation,
        List<Float> translation,
        ItemDisplayContext mode,
        Float sway,
        Map<String, SubTransformData> componentTransforms, // Map of int -> TransformData
        SecondaryTransformData secondaryTransforms,
        TertiaryTransformData tertiaryTransforms
) {
    public static final Codec<BackTransformData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("item").forGetter(BackTransformData::item),
            Codec.FLOAT.listOf().fieldOf("scale").orElseGet(() -> List.of(1.0f, 1.0f, 1.0f)).forGetter(BackTransformData::scale),
            Codec.FLOAT.listOf().fieldOf("rotation").orElseGet(() -> List.of(0.0f, 0.0f, 0.0f)).forGetter(BackTransformData::rotation),
            Codec.FLOAT.listOf().fieldOf("translation").orElseGet(() -> List.of(0.0f, 0.0f, 0.0f)).forGetter(BackTransformData::translation),
            Codec.STRING.fieldOf("mode").orElse("FIXED")
                    .xmap(ItemDisplayContext::valueOf, ItemDisplayContext::name)
                    .forGetter(BackTransformData::mode),
            Codec.FLOAT.fieldOf("sway").orElse(1.0F).forGetter(BackTransformData::sway),
            Codec.unboundedMap(Codec.STRING, SubTransformData.CODEC)
                    .fieldOf("componentTransforms").orElse(Map.of())
                    .forGetter(BackTransformData::componentTransforms),
            SecondaryTransformData.CODEC.fieldOf("secondary").orElse(new SecondaryTransformData(
                            Identifier.of("null"),
                            List.of(1.0f, 1.0f, 1.0f),
                            List.of(0.0f, 0.0f, 0.0f),
                            List.of(0.0f, 0.0f, 0.0f),
                            ItemDisplayContext.NONE
                    ))
                    .forGetter(BackTransformData::secondaryTransforms),
            TertiaryTransformData.CODEC.fieldOf("tertiary").orElse(new TertiaryTransformData(
                            Identifier.of("null"),
                            List.of(1.0f, 1.0f, 1.0f),
                            List.of(0.0f, 0.0f, 0.0f),
                            List.of(0.0f, 0.0f, 0.0f),
                            ItemDisplayContext.NONE
                    ))
                    .forGetter(BackTransformData::tertiaryTransforms)
    ).apply(instance, BackTransformData::new));

    // Sub-class to store transformations per component value
    public record SubTransformData(
            List<Float> scale,
            List<Float> rotation,
            List<Float> translation,
            ItemDisplayContext mode,
            Float sway,
            SecondaryTransformData secondaryTransforms,
            TertiaryTransformData tertiaryTransforms
    ) {
        public static final Codec<SubTransformData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.listOf().fieldOf("scale").orElseGet(() -> List.of(1.0f, 1.0f, 1.0f)).forGetter(SubTransformData::scale),
                Codec.FLOAT.listOf().fieldOf("rotation").orElseGet(() -> List.of(0.0f, 0.0f, 0.0f)).forGetter(SubTransformData::rotation),
                Codec.FLOAT.listOf().fieldOf("translation").orElseGet(() -> List.of(0.0f, 0.0f, 0.0f)).forGetter(SubTransformData::translation),
                Codec.STRING.fieldOf("mode").orElse("FIXED")
                        .xmap(ItemDisplayContext::valueOf, ItemDisplayContext::name)
                        .forGetter(SubTransformData::mode),
                Codec.FLOAT.fieldOf("sway").orElse(1.0F).forGetter(SubTransformData::sway),
                SecondaryTransformData.CODEC.fieldOf("secondary").orElse(new SecondaryTransformData(
                                Identifier.of("null"),
                                List.of(1.0f, 1.0f, 1.0f),
                                List.of(0.0f, 0.0f, 0.0f),
                                List.of(0.0f, 0.0f, 0.0f),
                                ItemDisplayContext.NONE
                        ))
                        .forGetter(SubTransformData::secondaryTransforms),
                TertiaryTransformData.CODEC.fieldOf("tertiary").orElse(new TertiaryTransformData(
                                Identifier.of("null"),
                                List.of(1.0f, 1.0f, 1.0f),
                                List.of(0.0f, 0.0f, 0.0f),
                                List.of(0.0f, 0.0f, 0.0f),
                                ItemDisplayContext.NONE
                        ))
                        .forGetter(SubTransformData::tertiaryTransforms)
        ).apply(instance, SubTransformData::new));
    }

    public record SecondaryTransformData(
            Identifier item,
            List<Float> scale,
            List<Float> rotation,
            List<Float> translation,
            ItemDisplayContext mode
    ) {
        public static final Codec<SecondaryTransformData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("model").forGetter(SecondaryTransformData::item),
                Codec.FLOAT.listOf().fieldOf("scale").orElseGet(() -> List.of(1.0f, 1.0f, 1.0f)).forGetter(SecondaryTransformData::scale),
                Codec.FLOAT.listOf().fieldOf("rotation").orElseGet(() -> List.of(0.0f, 0.0f, 0.0f)).forGetter(SecondaryTransformData::rotation),
                Codec.FLOAT.listOf().fieldOf("translation").orElseGet(() -> List.of(0.0f, 0.0f, 0.0f)).forGetter(SecondaryTransformData::translation),
                Codec.STRING.fieldOf("mode").orElse("FIXED")
                        .xmap(ItemDisplayContext::valueOf, ItemDisplayContext::name)
                        .forGetter(SecondaryTransformData::mode)
        ).apply(instance, SecondaryTransformData::new));
    }

    public record TertiaryTransformData(
            Identifier item,
            List<Float> scale,
            List<Float> rotation,
            List<Float> translation,
            ItemDisplayContext mode
    ) {
        public static final Codec<TertiaryTransformData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Identifier.CODEC.fieldOf("model").forGetter(TertiaryTransformData::item),
                Codec.FLOAT.listOf().fieldOf("scale").orElseGet(() -> List.of(1.0f, 1.0f, 1.0f)).forGetter(TertiaryTransformData::scale),
                Codec.FLOAT.listOf().fieldOf("rotation").orElseGet(() -> List.of(0.0f, 0.0f, 0.0f)).forGetter(TertiaryTransformData::rotation),
                Codec.FLOAT.listOf().fieldOf("translation").orElseGet(() -> List.of(0.0f, 0.0f, 0.0f)).forGetter(TertiaryTransformData::translation),
                Codec.STRING.fieldOf("mode").orElse("FIXED")
                        .xmap(ItemDisplayContext::valueOf, ItemDisplayContext::name)
                        .forGetter(TertiaryTransformData::mode)
        ).apply(instance, TertiaryTransformData::new));
    }
}
