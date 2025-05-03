package net.hollowed.combatamenities.util.json;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemDisplayContext;

import java.util.List;

import java.util.Map;

public record BeltTransformData(
        String item,
        List<Float> scale,
        List<Float> rotation,
        List<Float> translation,
        ItemDisplayContext mode,
        Float sway,
        Map<String, SubTransformData> componentTransforms, // Map of int -> TransformData
        Boolean flip
) {
    public static final Codec<BeltTransformData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("item").forGetter(BeltTransformData::item),
            Codec.FLOAT.listOf().fieldOf("scale").orElseGet(() -> List.of(1.0f, 1.0f, 1.0f)).forGetter(BeltTransformData::scale),
            Codec.FLOAT.listOf().fieldOf("rotation").orElseGet(() -> List.of(0.0f, 0.0f, 0.0f)).forGetter(BeltTransformData::rotation),
            Codec.FLOAT.listOf().fieldOf("translation").orElseGet(() -> List.of(0.0f, 0.0f, 0.0f)).forGetter(BeltTransformData::translation),
            Codec.STRING.fieldOf("mode").orElse("FIXED")
                    .xmap(ItemDisplayContext::valueOf, ItemDisplayContext::name)
                    .forGetter(BeltTransformData::mode),
            Codec.FLOAT.fieldOf("sway").orElse(1.0F).forGetter(BeltTransformData::sway),
            Codec.unboundedMap(Codec.STRING, SubTransformData.CODEC) // Map<Integer, SubTransformData>
                    .fieldOf("componentTransforms").orElse(Map.of())
                    .forGetter(BeltTransformData::componentTransforms),
            Codec.BOOL.fieldOf("flip").orElse(false).forGetter(BeltTransformData::flip)
    ).apply(instance, BeltTransformData::new));

    // Sub-class to store transformations per component value
    public record SubTransformData(
            List<Float> scale,
            List<Float> rotation,
            List<Float> translation,
            ItemDisplayContext mode,
            Float sway,
            Boolean flip
    ) {
        public static final Codec<SubTransformData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.FLOAT.listOf().fieldOf("scale").orElseGet(() -> List.of(1.0f, 1.0f, 1.0f)).forGetter(SubTransformData::scale),
                Codec.FLOAT.listOf().fieldOf("rotation").orElseGet(() -> List.of(0.0f, 0.0f, 0.0f)).forGetter(SubTransformData::rotation),
                Codec.FLOAT.listOf().fieldOf("translation").orElseGet(() -> List.of(0.0f, 0.0f, 0.0f)).forGetter(SubTransformData::translation),
                Codec.STRING.fieldOf("mode").orElse("FIXED")
                        .xmap(ItemDisplayContext::valueOf, ItemDisplayContext::name)
                        .forGetter(SubTransformData::mode),
                Codec.FLOAT.fieldOf("sway").orElse(1.0F).forGetter(SubTransformData::sway),
                Codec.BOOL.fieldOf("flip").orElse(false).forGetter(SubTransformData::flip)
        ).apply(instance, SubTransformData::new));
    }
}
