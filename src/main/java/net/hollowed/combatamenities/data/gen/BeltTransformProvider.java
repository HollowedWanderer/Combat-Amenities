package net.hollowed.combatamenities.data.gen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.hollowed.combatamenities.data.read.BeltTransformData;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@SuppressWarnings("unused")
public abstract class BeltTransformProvider extends FabricCodecDataProvider<BeltTransformData> {
    protected BeltTransformProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(dataOutput, registriesFuture, PackOutput.Target.RESOURCE_PACK, "beltslot_transforms", BeltTransformData.CODEC);
    }

    @Override
    protected void configure(@NotNull BiConsumer<Identifier, BeltTransformData> consumer, HolderLookup.@NotNull Provider provider) {
        this.generateTransforms(provider, consumer);
    }

    public abstract void generateTransforms(HolderLookup.Provider provider, BiConsumer<Identifier, BeltTransformData> consumer);

    @Override
    public @NotNull String getName() {
        return "Belt Transforms";
    }
}
