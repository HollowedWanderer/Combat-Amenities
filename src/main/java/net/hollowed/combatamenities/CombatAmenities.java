package net.hollowed.combatamenities;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.hollowed.combatamenities.config.ModConfig;
import net.hollowed.combatamenities.networking.*;
import net.hollowed.combatamenities.util.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Unique;

import java.util.Set;

public class CombatAmenities implements ModInitializer {
	public static final String MOD_ID = "combatamenities";

	public static ModConfig CONFIG = new ModConfig();

	public static final Set<String> DURABILITY_ENCHANTMENTS = Set.of(
			"Enchantment Unbreaking",
			"Enchantment Mending"
	);

	public static final Set<String> TRIDENT_ENCHANTMENTS = Set.of(
			"Enchantment Loyalty"
	);

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static Vec3d matrixToVec(MatrixStack matrixStack) {
		// Extract transformation matrix
		Matrix4f matrix = matrixStack.peek().getPositionMatrix();

		// Convert local position to world space
		Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
		return transformToWorld(matrix, camera);
	}

	private static Vec3d transformToWorld(Matrix4f matrix, Camera camera) {
		// Convert (0,0,0) in local item space to transformed coordinates
		Vector4f localPos = new Vector4f(0, 0, 0, 1);
		matrix.transform(localPos);

		// Convert view space to world space by adding the camera position
		Vec3d cameraPos = camera.getPos();
		return new Vec3d(cameraPos.x + localPos.x(), cameraPos.y + localPos.y(), cameraPos.z + localPos.z());
	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ModComponents.initialize();

		ServerTickEvents.END_SERVER_TICK.register(server -> TickDelayScheduler.tick());

		// Json stuff
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new TransformResourceReloadListener());
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new BeltTransformResourceReloadListener());

		PayloadTypeRegistry.playC2S().register(BackslotPacketPayload.ID, BackslotPacketPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(BeltslotPacketPayload.ID, BeltslotPacketPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(BackSlotInventoryPacketPayload.BACKSLOT_INVENTORY_PACKET_ID, BackSlotInventoryPacketPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(BeltSlotInventoryPacketPayload.BELTSLOT_INVENTORY_PACKET_ID, BeltSlotInventoryPacketPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(BackSlotCreativeClientPacketPayload.BACKSLOT_CREATIVE_CLIENT_PACKET_ID, BackSlotCreativeClientPacketPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BackSlotClientPacketPayload.BACKSLOT_CLIENT_PACKET_ID, BackSlotClientPacketPayload.CODEC);

		BackSlotInventoryPacketReceiver.registerServerPacket();
		BeltSlotInventoryPacketReceiver.registerServerPacket();
        BackSlotServerPacket.registerServerPacket();
		BeltSlotServerPacket.registerServerPacket();
		BackSlotCreativeClientPacket.registerClientPacket();

		// Config
		AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
		CONFIG = AutoConfig.getConfigHolder(ModConfig.class).getConfig();

		LOGGER.info("It is time for backing and slotting");
	}

	public static final GameRules.Key<GameRules.BooleanRule> KEEP_BACK_SLOT_ITEM =
			GameRuleRegistry.register("keepBackSlotItem", GameRules.Category.DROPS, GameRuleFactory.createBooleanRule(false));
}