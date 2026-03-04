package net.hollowed.combatamenities.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.hollowed.combatamenities.util.json.BackTransformData;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public abstract class BackTransformProvider extends FabricCodecDataProvider<BackTransformData> {
    protected BackTransformProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(dataOutput, registriesFuture, PackOutput.Target.RESOURCE_PACK, "backslot_transforms", BackTransformData.CODEC);
    }

    @Override
    protected void configure(BiConsumer<Identifier, BackTransformData> consumer, HolderLookup.Provider provider) {
        this.generateTransforms(provider, consumer);
    }

    public abstract void generateTransforms(HolderLookup.Provider provider, BiConsumer<Identifier, BackTransformData> consumer);

    @Override
    public String getName() {
        return "Back Transforms";
    }
}
