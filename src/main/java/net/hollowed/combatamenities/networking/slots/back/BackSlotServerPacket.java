package net.hollowed.combatamenities.networking.slots.back;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.util.interfaces.ItemSlotSoundHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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

            if (backStack.getItem() instanceof ItemSlotSoundHandler) {
                player.getWorld().playSound(null, player.getBlockPos(), ((ItemSlotSoundHandler) backStack.getItem()).combat_Amenities$getUnsheatheSound(), SoundCategory.PLAYERS, (CombatAmenities.CONFIG.backslotSwapSoundVolume / 100F), 0.9F);
            } else if (handStack.getItem() instanceof ItemSlotSoundHandler) {
                player.getWorld().playSound(null, player.getBlockPos(), ((ItemSlotSoundHandler) backStack.getItem()).combat_Amenities$getSheatheSound(), SoundCategory.PLAYERS, (CombatAmenities.CONFIG.backslotSwapSoundVolume / 100F), 0.9F);
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
                player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_ARMOR_EQUIP_CHAIN.value(), SoundCategory.PLAYERS, (CombatAmenities.CONFIG.backslotSwapSoundVolume / 100F), 1F);
            }

            // Sync the player's inventory back to the client
            player.currentScreenHandler.sendContentUpdates();
        })));
    }
}
