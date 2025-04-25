package net.hollowed.combatamenities.util.json;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TransformResourceReloadListener implements SimpleSynchronousResourceReloadListener {
    private static final Map<Identifier, TransformData> transforms = new HashMap<>();
    private static TransformData defaultTransforms;

    @Override
    public Identifier getFabricId() {
        return Identifier.of(CombatAmenities.MOD_ID, "backslot_transforms");
    }

    @Override
    public void reload(ResourceManager manager) {
        transforms.clear();
        CombatAmenities.LOGGER.info("Reloading transform data...");

        manager.findResources("backslot_transforms", path -> path.getPath().endsWith(".json")).keySet().forEach(id -> {
            if (manager.getResource(id).isPresent()) {
                try (InputStream stream = manager.getResource(id).get().getInputStream()) {
                    var json = JsonHelper.deserialize(new InputStreamReader(stream, StandardCharsets.UTF_8));
                    DataResult<TransformData> result = TransformData.CODEC.parse(JsonOps.INSTANCE, json);

                    result.resultOrPartial(CombatAmenities.LOGGER::error).ifPresent(data -> {
                        CombatAmenities.LOGGER.info("Loaded transform for: {}", data.item());
                        if (Objects.equals(data.item(), Identifier.of("backslot", "default"))) {
                            defaultTransforms = data;
                        } else {
                            transforms.put(data.item(), data);
                        }
                    });
                } catch (Exception e) {
                    CombatAmenities.LOGGER.error("Failed to load transform for {}: {}", id, e.getMessage());
                }
            }
        });

        CombatAmenities.LOGGER.info("Loaded transforms: {}", transforms);
    }

    public static TransformData getTransform(Identifier itemId, String component) {
        TransformData baseTransform = transforms.getOrDefault(itemId, defaultTransforms);

        if (baseTransform != null) {

            // Check if a specific component transformation exists
            if (baseTransform.componentTransforms().containsKey(component)) {
                TransformData.SubTransformData subTransform = baseTransform.componentTransforms().get(component);
                TransformData.SecondaryTransformData secondary = subTransform.secondaryTransforms();
                TransformData.TertiaryTransformData tertiary = subTransform.tertiaryTransforms();

                return new TransformData(
                        itemId,
                        subTransform.scale(),
                        subTransform.rotation(),
                        subTransform.translation(),
                        subTransform.mode(),
                        subTransform.sway(),
                        Map.of(),
                        new TransformData.SecondaryTransformData(
                                secondary.item(),
                                secondary.scale(),
                                secondary.rotation(),
                                secondary.translation(),
                                secondary.mode()
                        ),
                        new TransformData.TertiaryTransformData(
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
        return new TransformData(
                itemId,
                List.of(1.0f, 1.0f, 1.0f), // Default scale
                List.of(0.0f, 0.0f, 0.0f), // Default rotation
                List.of(0.0f, 0.0f, 0.0f), // Default translation
                ItemDisplayContext.FIXED, // Default mode
                1.0F, // Default sway
                Map.of(), // Empty component transforms
                new TransformData.SecondaryTransformData(
                        Identifier.of("null"),
                        List.of(1.0f, 1.0f, 1.0f), // Default scale
                        List.of(0.0f, 0.0f, 0.0f), // Default rotation
                        List.of(0.0f, 0.0f, 0.0f), // Default translation
                        ItemDisplayContext.FIXED // Default mode
                ),
                new TransformData.TertiaryTransformData(
                        Identifier.of("null"),
                        List.of(1.0f, 1.0f, 1.0f), // Default scale
                        List.of(0.0f, 0.0f, 0.0f), // Default rotation
                        List.of(0.0f, 0.0f, 0.0f), // Default translation
                        ItemDisplayContext.FIXED // Default mode
                )
        );
    }
}
