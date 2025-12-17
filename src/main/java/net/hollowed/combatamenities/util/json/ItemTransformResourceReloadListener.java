package net.hollowed.combatamenities.util.json;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.util.delay.ClientTickDelayScheduler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ItemTransformResourceReloadListener implements ResourceManagerReloadListener {
    private static final Map<Identifier, ItemTransformData> transforms = new HashMap<>();
    private static ItemTransformData defaultTransforms;

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager manager) {
        Minecraft.getInstance().execute(() -> this.actuallyLoad(manager));
    }

    private void actuallyLoad(ResourceManager manager) {
        ClientTickDelayScheduler.schedule(-1, () -> {
            transforms.clear();

            manager.listResources("item_transforms", path -> path.getPath().endsWith(".json")).keySet().forEach(id -> {
                if (manager.getResource(id).isPresent()) {
                    try (InputStream stream = manager.getResource(id).get().open()) {
                        var json = GsonHelper.parse(new InputStreamReader(stream, StandardCharsets.UTF_8));
                        DataResult<ItemTransformData> result = ItemTransformData.CODEC.parse(JsonOps.INSTANCE, json);

                        result.resultOrPartial(CombatAmenities.LOGGER::error).ifPresent(data -> {
                            CombatAmenities.LOGGER.info("{} and {}", data.sheatheId(), data.unsheatheId());
                            if (Objects.equals(data.item(), "default")) {
                                defaultTransforms = data;
                            } else if (data.item().startsWith("#")) {
                                // Remove the '#' prefix
                                String tagString = data.item().substring(1);
                                Identifier tagId = Identifier.parse(tagString);

                                TagKey<Item> tag = TagKey.create(BuiltInRegistries.ITEM.key(), tagId);

                                BuiltInRegistries.ITEM.forEach(item -> {
                                    Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
                                    if (item.getDefaultInstance().getItemHolder().is(tag)) {
                                        transforms.putIfAbsent(itemId, data);
                                    }
                                });
                            } else {
                                transforms.put(Identifier.parse(data.item()), data);
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
                SoundEvents.EMPTY.location(),
                SoundEvents.EMPTY.location()
        );
    }
}
