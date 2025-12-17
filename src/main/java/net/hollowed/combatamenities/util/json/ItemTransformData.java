package net.hollowed.combatamenities.util.json;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

public record ItemTransformData(
        String item,
        Identifier unsheatheId,
        Identifier sheatheId
) {
    public static final Codec<ItemTransformData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("item").forGetter(ItemTransformData::item),
            Identifier.CODEC.fieldOf("unsheatheId").forGetter(ItemTransformData::unsheatheId),
            Identifier.CODEC.fieldOf("sheatheId").forGetter(ItemTransformData::sheatheId)
    ).apply(instance, ItemTransformData::new));
}
