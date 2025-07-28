package net.hollowed.combatamenities.util.json;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.util.delay.ClientTickDelayScheduler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ItemTransformResourceReloadListener implements SimpleSynchronousResourceReloadListener {
    private static final Map<Identifier, ItemTransformData> transforms = new HashMap<>();
    private static ItemTransformData defaultTransforms;

    @Override
    public Identifier getFabricId() {
        return Identifier.of(CombatAmenities.MOD_ID, "item_transforms");
    }

    @Override
    public void reload(ResourceManager manager) {
        MinecraftClient.getInstance().execute(() -> this.actuallyLoad(manager));
    }

    private void actuallyLoad(ResourceManager manager) {
        ClientTickDelayScheduler.schedule(-1, () -> {
            transforms.clear();

            manager.findResources("item_transforms", path -> path.getPath().endsWith(".json")).keySet().forEach(id -> {
                if (manager.getResource(id).isPresent()) {
                    try (InputStream stream = manager.getResource(id).get().getInputStream()) {
                        var json = JsonHelper.deserialize(new InputStreamReader(stream, StandardCharsets.UTF_8));
                        DataResult<ItemTransformData> result = ItemTransformData.CODEC.parse(JsonOps.INSTANCE, json);

                        result.resultOrPartial(CombatAmenities.LOGGER::error).ifPresent(data -> {
                            CombatAmenities.LOGGER.info(data.sheatheId() + " and " + data.unsheatheId());
                            if (Objects.equals(data.item(), "default")) {
                                defaultTransforms = data;
                            } else if (data.item().startsWith("#")) {
                                // Remove the '#' prefix
                                String tagString = data.item().substring(1);
                                Identifier tagId = Identifier.of(tagString);

                                TagKey<Item> tag = TagKey.of(Registries.ITEM.getKey(), tagId);

                                if (tag != null) {
                                    Registries.ITEM.forEach(item -> {
                                        Identifier itemId = Registries.ITEM.getId(item);
                                        if (item.getDefaultStack().getRegistryEntry().isIn(tag)) {
                                            transforms.putIfAbsent(itemId, data);
                                        }
                                    });
                                } else {
                                    CombatAmenities.LOGGER.warn("Tag #{} not found while loading item transforms!", tagId);
                                }
                            } else {
                                transforms.put(Identifier.of(data.item()), data);
                            }
                        });
                    } catch (Exception e) {
                        CombatAmenities.LOGGER.error("Failed to load transform for {}: {}", id, e.getMessage());
                    }
                }
            });
        });
    }

    public static ItemTransformData getTransform(Identifier itemId) {
        ItemTransformData transformData = transforms.getOrDefault(itemId, defaultTransforms);
        if (transformData != null) {
            return transformData;
        }
        return new ItemTransformData(
                itemId.toString(),
                SoundEvents.INTENTIONALLY_EMPTY.id(),
                SoundEvents.INTENTIONALLY_EMPTY.id()
        );
    }
}
