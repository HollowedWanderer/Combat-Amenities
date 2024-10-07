package net.hollowed.backslot.networking;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.backslot.ModSounds;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

import static net.hollowed.backslot.Backslot.MOD_ID;

public class BackSlotPacket {

    public static final Identifier BACKSLOT_PACKET_ID = new Identifier(MOD_ID, "backslot_packet");

    public static void send() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        ClientPlayNetworking.send(BACKSLOT_PACKET_ID, buf);
    }

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(BACKSLOT_PACKET_ID, (server, player, handler, buf, responseSender) -> {
            server.execute(() -> {
                if (player == null || player.currentScreenHandler == null) {
                    return;
                }

                ItemStack offhandStack = player.getOffHandStack();
                ItemStack handStack = player.getMainHandStack();
                ItemStack backStack = player.getInventory().getStack(41);

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


                if (backStack.getItem() instanceof AxeItem) {
                    player.getWorld().playSoundFromEntity(null, player, ModSounds.SWORD_UNSHEATH, SoundCategory.PLAYERS, 1F, 0.9F);
                    player.getWorld().playSoundFromEntity(null, player, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, SoundCategory.PLAYERS, 1F, 0.9F);
                } else if (backStack.getItem() instanceof SwordItem) {
                    player.getWorld().playSoundFromEntity(null, player, ModSounds.SWORD_UNSHEATH, SoundCategory.PLAYERS, 1F, 1.0F);
                    player.getWorld().playSoundFromEntity(null, player, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, SoundCategory.PLAYERS, 1F, 1F);
                } else if (backStack != ItemStack.EMPTY) {
                    player.getWorld().playSoundFromEntity(null, player, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, SoundCategory.PLAYERS, 1F, 1F);
                }

                if (offhandStack != ItemStack.EMPTY || handStack != ItemStack.EMPTY) {
                    player.getWorld().playSoundFromEntity(null, player, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, SoundCategory.PLAYERS, 1F, 1F);
                }

                // Sync the player's inventory back to the client
                player.currentScreenHandler.sendContentUpdates();
            });
        });
    }
}