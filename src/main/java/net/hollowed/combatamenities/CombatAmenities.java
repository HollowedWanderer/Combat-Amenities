package net.hollowed.combatamenities;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.hollowed.combatamenities.config.CAConfig;
import net.hollowed.combatamenities.networking.slots.SlotClientPacketPayload;
import net.hollowed.combatamenities.networking.slots.SlotCreativeClientPacket;
import net.hollowed.combatamenities.networking.slots.SlotCreativeClientPacketPayload;
import net.hollowed.combatamenities.networking.slots.SoundPacketPayload;
import net.hollowed.combatamenities.networking.slots.back.BackSlotInventoryPacketPayload;
import net.hollowed.combatamenities.networking.slots.back.BackSlotInventoryPacketReceiver;
import net.hollowed.combatamenities.networking.slots.back.BackSlotServerPacket;
import net.hollowed.combatamenities.networking.slots.back.BackslotPacketPayload;
import net.hollowed.combatamenities.networking.slots.belt.BeltSlotInventoryPacketPayload;
import net.hollowed.combatamenities.networking.slots.belt.BeltSlotInventoryPacketReceiver;
import net.hollowed.combatamenities.networking.slots.belt.BeltSlotServerPacket;
import net.hollowed.combatamenities.networking.slots.belt.BeltslotPacketPayload;
import net.hollowed.combatamenities.index.CAParticles;
import net.hollowed.combatamenities.index.CASounds;
import net.hollowed.combatamenities.util.delay.TickDelayScheduler;
import net.hollowed.combatamenities.util.items.ModComponents;
import net.hollowed.combatamenities.util.json.BeltTransformResourceReloadListener;
import net.hollowed.combatamenities.util.json.BackTransformResourceReloadListener;
import net.hollowed.combatamenities.util.json.ItemTransformResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class CombatAmenities implements ModInitializer {
	public static final String MOD_ID = "combatamenities";

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

	@SuppressWarnings("unused")
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

		CAParticles.initialize();
		ModComponents.initialize();
		CASounds.initialize();

		ServerTickEvents.END_SERVER_TICK.register(server -> TickDelayScheduler.tick());

		// Json stuff
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new BackTransformResourceReloadListener());
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new BeltTransformResourceReloadListener());
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new ItemTransformResourceReloadListener());

		PayloadTypeRegistry.playC2S().register(BackslotPacketPayload.ID, BackslotPacketPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(BeltslotPacketPayload.ID, BeltslotPacketPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(BackSlotInventoryPacketPayload.ID, BackSlotInventoryPacketPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(BeltSlotInventoryPacketPayload.ID, BeltSlotInventoryPacketPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(SlotCreativeClientPacketPayload.ID, SlotCreativeClientPacketPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SlotClientPacketPayload.ID, SlotClientPacketPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(SoundPacketPayload.ID, SoundPacketPayload.CODEC);

		BackSlotInventoryPacketReceiver.registerServerPacket();
		BeltSlotInventoryPacketReceiver.registerServerPacket();
        BackSlotServerPacket.registerServerPacket();
		BeltSlotServerPacket.registerServerPacket();
		SlotCreativeClientPacket.registerClientPacket();

		// Config
		MidnightConfig.init(MOD_ID, CAConfig.class);

		LOGGER.info("It is time for backing and slotting");
	}

	public static final GameRules.Key<GameRules.BooleanRule> KEEP_BACK_SLOT_ITEM =
			GameRuleRegistry.register("keepBackSlotItem", GameRules.Category.DROPS, GameRuleFactory.createBooleanRule(false));
}