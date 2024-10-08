package net.hollowed.backslot.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.backslot.ModSounds;
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
    }
}
