package net.hollowed.combatamenities.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.util.ItemSlotSoundHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;

public class BeltSlotServerPacket {

    public static void registerServerPacket() {
        ServerPlayNetworking.registerGlobalReceiver(BeltslotPacketPayload.ID, ((payload, context) -> context.server().execute(() -> {
            PlayerEntity player = context.player();
            if (player == null || player.currentScreenHandler == null) {
                return;
            }

            ItemStack offhandStack = player.getOffHandStack();
            ItemStack handStack = player.getMainHandStack();
            ItemStack backStack = player.getInventory().getStack(42);

            if (backStack.getItem() instanceof ItemSlotSoundHandler item) {
                player.getWorld().playSound(null, player.getBlockPos(), item.combat_Amenities$getUnsheatheSound(), SoundCategory.PLAYERS, (CombatAmenities.CONFIG.backslotSwapSoundVolume / 100F), 0.9F);
            } else if (handStack.getItem() instanceof ItemSlotSoundHandler item) {
                player.getWorld().playSound(null, player.getBlockPos(), item.combat_Amenities$getSheatheSound(), SoundCategory.PLAYERS, (CombatAmenities.CONFIG.backslotSwapSoundVolume / 100F), 0.9F);
            }

            if (!handStack.isEmpty()) {
                player.setStackInHand(Hand.MAIN_HAND, backStack.copy());
                player.getInventory().setStack(42, handStack.copy());
            } else {
                if (backStack.isEmpty()) {
                    player.setStackInHand(Hand.OFF_HAND, backStack.copy());
                    player.getInventory().setStack(42, offhandStack.copy());
                } else {
                    player.setStackInHand(Hand.MAIN_HAND, backStack.copy());
                    player.getInventory().setStack(42, handStack.copy());
                }
            }

            if (!offhandStack.isEmpty() || !handStack.isEmpty() || !backStack.isEmpty()) {
                player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_CHAIN.value(), SoundCategory.PLAYERS, (CombatAmenities.CONFIG.backslotSwapSoundVolume / 100F), 1F);
            }

            // Sync the player's inventory back to the client
            player.currentScreenHandler.sendContentUpdates();
        })));
    }
}
