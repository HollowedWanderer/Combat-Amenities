package net.hollowed.backslot.util;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.hollowed.backslot.Backslot;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransformResourceReloadListener implements SimpleSynchronousResourceReloadListener {
    private static final Map<Identifier, TransformData> transforms = new HashMap<>();

    @Override
    public Identifier getFabricId() {
        return Identifier.of(Backslot.MOD_ID, "backslot_transforms");
    }

    @Override
    public void reload(ResourceManager manager) {
        transforms.clear();
        System.out.println("Reloading transform data...");

        manager.findResources("backslot_transforms", path -> path.getPath().endsWith(".json")).keySet().forEach(id -> {
            try (InputStream stream = manager.getResource(id).get().getInputStream()) {
                var json = JsonHelper.deserialize(new InputStreamReader(stream, StandardCharsets.UTF_8));
                DataResult<TransformData> result = TransformData.CODEC.parse(JsonOps.INSTANCE, json);

                result.resultOrPartial(System.err::println).ifPresent(data -> {
                    System.out.println("Loaded transform for: " + data.item());
                    transforms.put(data.item(), data);
                });
            } catch (Exception e) {
                System.err.println("Failed to load transform for " + id + ": " + e.getMessage());
            }
        });

        System.out.println("Loaded transforms: " + transforms);
    }

    public static TransformData getTransform(Identifier itemId) {
        // Provide a default TransformData with Lists for scale, rotation, and translation
        return transforms.getOrDefault(itemId, new TransformData(
                itemId,
                List.of(1.0f, 1.0f, 1.0f), // Default scale
                List.of(0.0f, 0.0f, 0.0f), // Default rotation
                List.of(0.0f, 0.0f, 0.0f)  // Default translation
        ));
    }
}
