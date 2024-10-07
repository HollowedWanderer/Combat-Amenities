package net.hollowed.backslot;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.backslot.networking.BackslotPacketPayload;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
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

		ServerPlayNetworking.registerGlobalReceiver(BackslotPacketPayload.ID, ((payload, context) -> context.server().execute(() -> {
            PlayerEntity player = context.player();
            if (player == null || player.currentScreenHandler == null) {
                return;
            }

            ItemStack offhandStack = player.getOffHandStack();
            ItemStack handStack = player.getMainHandStack();
            ItemStack backStack = player.getInventory().getStack(41);

            if (backStack.getItem() instanceof AxeItem) {
                player.getWorld().playSoundFromEntity(null, player, ModSounds.SWORD_UNSHEATH, SoundCategory.PLAYERS, 1F, 0.9F);
                context.player().getWorld().playSound(null, context.player().getX(), context.player().getY(), context.player().getZ(), SoundEvents.ITEM_ARMOR_EQUIP_CHAIN,
                        SoundCategory.PLAYERS, 1F, 1F, context.player().getWorld().getRandom().nextLong());
            } else if (backStack.getItem() instanceof SwordItem) {
                player.getWorld().playSoundFromEntity(null, player, ModSounds.SWORD_UNSHEATH, SoundCategory.PLAYERS, 1F, 1.0F);
                context.player().getWorld().playSound(null, context.player().getX(), context.player().getY(), context.player().getZ(), SoundEvents.ITEM_ARMOR_EQUIP_CHAIN,
                        SoundCategory.PLAYERS, 1F, 1F, context.player().getWorld().getRandom().nextLong());
            } else if (backStack != ItemStack.EMPTY) {
                context.player().getWorld().playSound(null, context.player().getX(), context.player().getY(), context.player().getZ(), SoundEvents.ITEM_ARMOR_EQUIP_CHAIN,
                        SoundCategory.PLAYERS, 1F, 1F, context.player().getWorld().getRandom().nextLong());
            }

            if (!handStack.isEmpty()) {
                player.setStackInHand(Hand.MAIN_HAND, backStack.copy());
                player.getInventory().setStack(41, handStack.copy());
            } else {
                if (backStack.isEmpty()) {
                    player.setStackInHand(Hand.OFF_HAND, backStack.copy());
                    player.getInventory().setStack(41, offhandStack.copy());
                } else {
                    player.setStackInHand(Hand.MAIN_HAND, backStack.copy());
                    player.getInventory().setStack(41, handStack.copy());
                }
            }

            if (offhandStack != ItemStack.EMPTY || handStack != ItemStack.EMPTY) {
                context.player().getWorld().playSound(null, context.player().getX(), context.player().getY(), context.player().getZ(), SoundEvents.ITEM_ARMOR_EQUIP_CHAIN,
                        SoundCategory.PLAYERS, 1F, 1F, context.player().getWorld().getRandom().nextLong());
            }

            System.out.println("PACKET RECEIVED");

            // Sync the player's inventory back to the client
            player.currentScreenHandler.sendContentUpdates();
        })));

		LOGGER.info("It is time for backing and slotting");
	}
}