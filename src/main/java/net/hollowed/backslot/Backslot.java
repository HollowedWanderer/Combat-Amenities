package net.hollowed.backslot;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.backslot.networking.BackSlotClientPacketPayload;
import net.hollowed.backslot.networking.BackSlotServerPacket;
import net.hollowed.backslot.networking.BackslotPacketPayload;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Backslot implements ModInitializer {
	public static final String MOD_ID = "combatamenities";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

        PayloadTypeRegistry.playC2S().register(BackslotPacketPayload.ID, BackslotPacketPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BackSlotClientPacketPayload.BACKSLOT_CLIENT_PACKET_ID, BackSlotClientPacketPayload.CODEC);

        BackSlotServerPacket.registerServerPacket();

		LOGGER.info("It is time for backing and slotting");
	}
}