package net.hollowed.combatamenities.networking.slots.belt;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.combatamenities.networking.slots.SoundPacketPayload;
import net.hollowed.combatamenities.util.items.CAComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BeltSlotServerPacket {
    public static void registerServerPacket() {
        ServerPlayNetworking.registerGlobalReceiver(BeltslotPacketPayload.ID, ((payload, context) -> context.server().execute(() -> {
            Player player = context.player();

            ItemStack offhandStack = player.getOffhandItem();
            ItemStack handStack = player.getMainHandItem();
            ItemStack beltStack = player.getItemBySlot(EquipmentSlot.COMBATAMENITIES_BELTSLOT);

            if (player instanceof ServerPlayer serverPlayer) {
                for (ServerPlayer serverPlayerTemp : serverPlayer.level().players()) {
                    if (!beltStack.isEmpty()) {
                        ServerPlayNetworking.send(serverPlayerTemp, new SoundPacketPayload(0, player.position(), true, 1.0F, 1.0F, 1, beltStack));
                    }
                    if (!handStack.isEmpty() || (!offhandStack.isEmpty() && beltStack.isEmpty())) {
                        ServerPlayNetworking.send(serverPlayerTemp, new SoundPacketPayload(0, player.position(), true, 1.0F, 1.0F, 2, !handStack.isEmpty() ? handStack : offhandStack));
                    }
                }
            }

            if (!handStack.isEmpty()) {
                player.setItemInHand(InteractionHand.MAIN_HAND, beltStack);
                player.setItemSlot(EquipmentSlot.COMBATAMENITIES_BELTSLOT, handStack);
            } else {
                if (beltStack.isEmpty()) {
                    player.setItemInHand(InteractionHand.OFF_HAND, beltStack);
                    player.setItemSlot(EquipmentSlot.COMBATAMENITIES_BELTSLOT, offhandStack);
                } else {
                    player.setItemInHand(InteractionHand.MAIN_HAND, beltStack);
                    player.setItemSlot(EquipmentSlot.COMBATAMENITIES_BELTSLOT, handStack);
                }
            }

            player.containerMenu.broadcastChanges();
        })));
    }
}
