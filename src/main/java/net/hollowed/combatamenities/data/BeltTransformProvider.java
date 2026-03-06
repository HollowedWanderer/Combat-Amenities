package net.hollowed.combatamenities.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.hollowed.combatamenities.util.json.BeltTransformData;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public abstract class BeltTransformProvider extends FabricCodecDataProvider<BeltTransformData> {
    protected BeltTransformProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(dataOutput, registriesFuture, PackOutput.Target.RESOURCE_PACK, "beltslot_transforms", BeltTransformData.CODEC);
    }

    @Override
    protected void configure(BiConsumer<Identifier, BeltTransformData> consumer, HolderLookup.Provider provider) {
        this.generateTransforms(provider, consumer);
    }

    public abstract void generateTransforms(HolderLookup.Provider provider, BiConsumer<Identifier, BeltTransformData> consumer);

    @Override
    public String getName() {
        return "Belt Transforms";
    }
}
