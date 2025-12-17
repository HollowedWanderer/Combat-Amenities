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
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeltTransformResourceReloadListener implements ResourceManagerReloadListener {
    private static final Map<Identifier, BeltTransformData> transforms = new HashMap<>();
    private static BeltTransformData defaultTransforms;

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager manager) {
        Minecraft.getInstance().execute(() -> this.actuallyLoad(manager));
    }

    private void actuallyLoad(ResourceManager manager) {
        ClientTickDelayScheduler.schedule(-1, () -> {
            transforms.clear();

            manager.listResources("beltslot_transforms", path -> path.getPath().endsWith(".json")).keySet().forEach(id -> {
                if (manager.getResource(id).isPresent()) {
                    try (InputStream stream = manager.getResource(id).get().open()) {
                        var json = GsonHelper.parse(new InputStreamReader(stream, StandardCharsets.UTF_8));
                        DataResult<BeltTransformData> result = BeltTransformData.CODEC.parse(JsonOps.INSTANCE, json);

                        result.resultOrPartial(CombatAmenities.LOGGER::error).ifPresent(data -> {
                            if (data.item().equals("default")) {
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

    public static BeltTransformData getTransform(Identifier itemId, String component) {
        BeltTransformData baseTransform = transforms.getOrDefault(itemId, defaultTransforms);

        if (baseTransform != null) {
            // Check if a specific component transformation exists
            if (baseTransform.componentTransforms().containsKey(component)) {
                BeltTransformData.SubTransformData subTransform = baseTransform.componentTransforms().get(component);
                BeltTransformData.SecondaryTransformData secondary = subTransform.secondaryTransforms();
                BeltTransformData.TertiaryTransformData tertiary = subTransform.tertiaryTransforms();

                return new BeltTransformData(
                        itemId.toString(), // Preserve itemId
                        subTransform.scale(),
                        subTransform.rotation(),
                        subTransform.translation(),
                        subTransform.mode(),
                        subTransform.sway(),
                        Map.of(), // Subcomponents don't need to be passed
                        subTransform.flip(),
                        new BeltTransformData.SecondaryTransformData(
                                secondary.item(),
                                secondary.scale(),
                                secondary.rotation(),
                                secondary.translation(),
                                secondary.mode()
                        ),
                        new BeltTransformData.TertiaryTransformData(
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
        return new BeltTransformData(
                itemId.toString(),
                List.of(1.0f, 1.0f, 1.0f), // Default scale
                List.of(0.0f, 0.0f, 0.0f), // Default rotation
                List.of(0.0f, 0.0f, 0.0f), // Default translation
                ItemDisplayContext.FIXED, // Default mode
                1.0F, // Default sway
                Map.of(), // Empty component transforms
                false,
                new BeltTransformData.SecondaryTransformData(
                        Identifier.parse("null"),
                        List.of(1.0f, 1.0f, 1.0f), // Default scale
                        List.of(0.0f, 0.0f, 0.0f), // Default rotation
                        List.of(0.0f, 0.0f, 0.0f), // Default translation
                        ItemDisplayContext.FIXED // Default mode
                ),
                new BeltTransformData.TertiaryTransformData(
                        Identifier.parse("null"),
                        List.of(1.0f, 1.0f, 1.0f), // Default scale
                        List.of(0.0f, 0.0f, 0.0f), // Default rotation
                        List.of(0.0f, 0.0f, 0.0f), // Default translation
                        ItemDisplayContext.FIXED // Default mode
                )
        );
    }
}
