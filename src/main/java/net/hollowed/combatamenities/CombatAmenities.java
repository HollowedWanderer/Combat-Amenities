package net.hollowed.combatamenities;

import com.mojang.blaze3d.vertex.PoseStack;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.hollowed.combatamenities.config.CAConfig;
import net.hollowed.combatamenities.networking.slots.SlotClientPacketPayload;
import net.hollowed.combatamenities.networking.slots.SlotCreativeClientPacket;
import net.hollowed.combatamenities.networking.slots.SlotCreativeClientPacketPayload;
import net.hollowed.combatamenities.networking.slots.SoundPacketPayload;
import net.hollowed.combatamenities.networking.slots.back.BackSlotServerPacket;
import net.hollowed.combatamenities.networking.slots.back.BackslotPacketPayload;
import net.hollowed.combatamenities.networking.slots.belt.BeltSlotServerPacket;
import net.hollowed.combatamenities.networking.slots.belt.BeltslotPacketPayload;
import net.hollowed.combatamenities.index.CAParticles;
import net.hollowed.combatamenities.index.CASounds;
import net.hollowed.combatamenities.util.delay.TickDelayScheduler;
import net.hollowed.combatamenities.util.items.CAComponents;
import net.hollowed.combatamenities.util.json.BeltTransformResourceReloadListener;
import net.hollowed.combatamenities.util.json.BackTransformResourceReloadListener;
import net.hollowed.combatamenities.util.json.ItemTransformResourceReloadListener;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
import net.minecraft.world.phys.Vec3;
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

	public static Identifier id(String string) {
		return Identifier.fromNamespaceAndPath(MOD_ID, string);
	}

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@SuppressWarnings("unused")
	public static Vec3 matrixToVec(PoseStack matrixStack) {
		// Extract transformation matrix
		Matrix4f matrix = matrixStack.last().pose();

		// Convert local position to world space
		Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
		return transformToWorld(matrix, camera);
	}

	private static Vec3 transformToWorld(Matrix4f matrix, Camera camera) {
		// Convert (0,0,0) in local item space to transformed coordinates
		Vector4f localPos = new Vector4f(0, 0, 0, 1);
		matrix.transform(localPos);

		// Convert view space to world space by adding the camera position
		Vec3 cameraPos = camera.position();
		return new Vec3(cameraPos.x + localPos.x(), cameraPos.y + localPos.y(), cameraPos.z + localPos.z());
	}

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		CAParticles.initialize();
		CAComponents.initialize();
		CASounds.initialize();

		ServerTickEvents.END_SERVER_TICK.register(server -> TickDelayScheduler.tick());

		// Json stuff
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(id("back_transforms"), new BackTransformResourceReloadListener());
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(id("belt_transforms"), new BeltTransformResourceReloadListener());
		ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloader(id("item_transforms"), new ItemTransformResourceReloadListener());

		PayloadTypeRegistry.playC2S().register(BackslotPacketPayload.ID, BackslotPacketPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(BeltslotPacketPayload.ID, BeltslotPacketPayload.CODEC);
		PayloadTypeRegistry.playC2S().register(SlotCreativeClientPacketPayload.ID, SlotCreativeClientPacketPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SlotClientPacketPayload.ID, SlotClientPacketPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(SoundPacketPayload.ID, SoundPacketPayload.CODEC);

        BackSlotServerPacket.registerServerPacket();
		BeltSlotServerPacket.registerServerPacket();
		SlotCreativeClientPacket.registerClientPacket();

		// Config
		MidnightConfig.init(MOD_ID, CAConfig.class);

		LOGGER.info("It is time for backing and slotting");
	}

	public static final GameRule<Boolean> KEEP_BACK_SLOT_ITEM = GameRuleBuilder
			.forBoolean(false)
			.category(GameRuleCategory.PLAYER)
			.buildAndRegister(id("keep_back_item"));
}