package net.hollowed.combatamenities.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.hollowed.combatamenities.util.json.ItemTransformData;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public abstract class ItemTransformProvider extends FabricCodecDataProvider<ItemTransformData> {
    protected ItemTransformProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(dataOutput, registriesFuture, PackOutput.Target.RESOURCE_PACK, "item_transforms", ItemTransformData.CODEC);
    }

    @Override
    protected void configure(BiConsumer<Identifier, ItemTransformData> consumer, HolderLookup.Provider provider) {
        this.generateTransforms(provider, consumer);
    }

    public abstract void generateTransforms(HolderLookup.Provider provider, BiConsumer<Identifier, ItemTransformData> consumer);

    @Override
    public String getName() {
        return "Item Transforms";
    }
}
