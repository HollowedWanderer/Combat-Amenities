package net.hollowed.combatamenities.util.json;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.util.delay.ClientTickDelayScheduler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackTransformResourceReloadListener implements SynchronousResourceReloader {
    private static final Map<Identifier, BackTransformData> transforms = new HashMap<>();
    private static BackTransformData defaultTransforms;

    @Override
    public void reload(ResourceManager manager) {
        MinecraftClient.getInstance().execute(() -> this.actuallyLoad(manager));
    }

    public void actuallyLoad(ResourceManager manager) {
        ClientTickDelayScheduler.schedule(-1, () -> {
            transforms.clear();

            manager.findResources("backslot_transforms", path -> path.getPath().endsWith(".json")).keySet().forEach(id -> {
                if (manager.getResource(id).isPresent()) {
                    try (InputStream stream = manager.getResource(id).get().getInputStream()) {
                        var json = JsonHelper.deserialize(new InputStreamReader(stream, StandardCharsets.UTF_8));
                        DataResult<BackTransformData> result = BackTransformData.CODEC.parse(JsonOps.INSTANCE, json);

                        result.resultOrPartial(CombatAmenities.LOGGER::error).ifPresent(data -> {
                            if (data.item().equals("default")) {
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

    public static BackTransformData getTransform(Identifier itemId, String component) {
        BackTransformData baseTransform = transforms.getOrDefault(itemId, defaultTransforms);

        if (baseTransform != null) {

            // Check if a specific component transformation exists
            if (baseTransform.componentTransforms().containsKey(component)) {
                BackTransformData.SubTransformData subTransform = baseTransform.componentTransforms().get(component);
                BackTransformData.SecondaryTransformData secondary = subTransform.secondaryTransforms();
                BackTransformData.TertiaryTransformData tertiary = subTransform.tertiaryTransforms();

                return new BackTransformData(
                        itemId.toString(),
                        subTransform.scale(),
                        subTransform.rotation(),
                        subTransform.translation(),
                        subTransform.mode(),
                        subTransform.sway(),
                        baseTransform.noFlip(),
                        Map.of(),
                        new BackTransformData.SecondaryTransformData(
                                secondary.item(),
                                secondary.scale(),
                                secondary.rotation(),
                                secondary.translation(),
                                secondary.mode()
                        ),
                        new BackTransformData.TertiaryTransformData(
                                tertiary.item(),
                                tertiary.scale(),
                                tertiary.rotation(),
                                tertiary.translation(),
                                tertiary.mode()
                        )
                );
            }
            return baseTransform;
        }

        // Fallback to a fully default transform if no data is available
        return new BackTransformData(
                itemId.toString(),
                List.of(1.0f, 1.0f, 1.0f), // Default scale
                List.of(0.0f, 0.0f, 0.0f), // Default rotation
                List.of(0.0f, 0.0f, 0.0f), // Default translation
                ItemDisplayContext.FIXED, // Default mode
                1.0F, // Default sway
                false,
                Map.of(), // Empty component transforms
                new BackTransformData.SecondaryTransformData(
                        Identifier.of("null"),
                        List.of(1.0f, 1.0f, 1.0f), // Default scale
                        List.of(0.0f, 0.0f, 0.0f), // Default rotation
                        List.of(0.0f, 0.0f, 0.0f), // Default translation
                        ItemDisplayContext.FIXED // Default mode
                ),
                new BackTransformData.TertiaryTransformData(
                        Identifier.of("null"),
                        List.of(1.0f, 1.0f, 1.0f), // Default scale
                        List.of(0.0f, 0.0f, 0.0f), // Default rotation
                        List.of(0.0f, 0.0f, 0.0f), // Default translation
                        ItemDisplayContext.FIXED // Default mode
                )
        );
    }
}
