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

public class BackTransformResourceReloadListener implements ResourceManagerReloadListener {
    private static final Map<Identifier, BackTransformData> transforms = new HashMap<>();
    private static BackTransformData defaultTransforms;

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager manager) {
        Minecraft.getInstance().execute(() -> this.actuallyLoad(manager));
    }

    public void actuallyLoad(ResourceManager manager) {
        ClientTickDelayScheduler.schedule(-1, () -> {
            transforms.clear();

            manager.listResources("backslot_transforms", path -> path.getPath().endsWith(".json")).keySet().forEach(id -> {
                if (manager.getResource(id).isPresent()) {
                    try (InputStream stream = manager.getResource(id).get().open()) {
                        var json = GsonHelper.parse(new InputStreamReader(stream, StandardCharsets.UTF_8));
                        DataResult<BackTransformData> result = BackTransformData.CODEC.parse(JsonOps.INSTANCE, json);

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
                        Identifier.parse("null"),
                        List.of(1.0f, 1.0f, 1.0f), // Default scale
                        List.of(0.0f, 0.0f, 0.0f), // Default rotation
                        List.of(0.0f, 0.0f, 0.0f), // Default translation
                        ItemDisplayContext.FIXED // Default mode
                ),
                new BackTransformData.TertiaryTransformData(
                        Identifier.parse("null"),
                        List.of(1.0f, 1.0f, 1.0f), // Default scale
                        List.of(0.0f, 0.0f, 0.0f), // Default rotation
                        List.of(0.0f, 0.0f, 0.0f), // Default translation
                        ItemDisplayContext.FIXED // Default mode
                )
        );
    }
}
