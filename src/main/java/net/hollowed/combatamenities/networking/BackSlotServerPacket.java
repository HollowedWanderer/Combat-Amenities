package net.hollowed.combatamenities.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.ModSounds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;

public class BackSlotServerPacket {

    public static void registerServerPacket() {
        ServerPlayNetworking.registerGlobalReceiver(BackslotPacketPayload.ID, ((payload, context) -> context.server().execute(() -> {
            PlayerEntity player = context.player();
            if (player == null || player.currentScreenHandler == null) {
                return;
            }

            ItemStack offhandStack = player.getOffHandStack();
            ItemStack handStack = player.getMainHandStack();
            ItemStack backStack = player.getInventory().getStack(41);

            if (backStack.getItem() instanceof AxeItem) {
                player.getWorld().playSound(null, player.getBlockPos(), ModSounds.SWORD_UNSHEATH, SoundCategory.PLAYERS, (CombatAmenities.CONFIG.backslotSwapSoundVolume / 100), 0.9F);
            } else if (backStack.getItem() instanceof SwordItem) {
                player.getWorld().playSound(null, player.getBlockPos(), ModSounds.SWORD_UNSHEATH, SoundCategory.PLAYERS, (CombatAmenities.CONFIG.backslotSwapSoundVolume / 100), 1.0F);
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

            if (!offhandStack.isEmpty() || !handStack.isEmpty() || !backStack.isEmpty()) {
                player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_CHAIN.value(), SoundCategory.PLAYERS, (CombatAmenities.CONFIG.backslotSwapSoundVolume / 100), 1F);
            }

            // Sync the player's inventory back to the client
            player.currentScreenHandler.sendContentUpdates();
        })));
    }
}
