package net.hollowed.combatamenities.mixin.slots.registration;

import net.hollowed.combatamenities.config.CAConfig;
import net.hollowed.combatamenities.util.json.ItemTransformData;
import net.hollowed.combatamenities.util.json.ItemTransformResourceReloadListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {

    @Shadow @Final public DefaultedList<Slot> slots;

    @Inject(method = "internalOnSlotClick", at = @At("HEAD"))
    private void internalOnSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        PlayerInventory playerInventory = player.getInventory();
        if (actionType == SlotActionType.SWAP && (button == 41 || button == 42)) {
            if (button == 41 && slotIndex == 46 || button == 42 && slotIndex == 47) return;
            ItemStack itemStack5 = playerInventory.getStack(button);
            Slot slot = this.slots.get(slotIndex);
            ItemStack itemStack = slot.getStack();
            if (!itemStack5.isEmpty() || !itemStack.isEmpty()) {
                if (itemStack5.isEmpty()) {
                    if (slot.canTakeItems(player)) {
                        playerInventory.setStack(button, itemStack);
                        slot.setStack(ItemStack.EMPTY);
                        slot.onTakeItem(player, itemStack);

                        playSound(player, itemStack, 1);
                    }
                } else if (itemStack.isEmpty()) {
                    if (slot.canInsert(itemStack5)) {
                        int p = slot.getMaxItemCount(itemStack5);
                        if (itemStack5.getCount() > p) {
                            slot.setStack(itemStack5.split(p));
                        } else {
                            playerInventory.setStack(button, ItemStack.EMPTY);
                            slot.setStack(itemStack5);

                            playSound(player, itemStack5, 2);
                        }
                    }
                } else if (slot.canTakeItems(player) && slot.canInsert(itemStack5)) {
                    int p = slot.getMaxItemCount(itemStack5);
                    if (itemStack5.getCount() > p) {
                        slot.setStack(itemStack5.split(p));
                        slot.onTakeItem(player, itemStack);
                        if (!playerInventory.insertStack(itemStack)) {
                            player.dropItem(itemStack, true);
                        }
                    } else {
                        playerInventory.setStack(button, itemStack);
                        slot.setStack(itemStack5);
                        slot.onTakeItem(player, itemStack);

                        playSound(player, itemStack, 1);
                        playSound(player, itemStack5, 2);
                    }
                }
            }
        }
    }

    @Unique
    private void playSound(PlayerEntity player, ItemStack stack, int soundSelector) {
        if (player == null || !player.getEntityWorld().isClient()) return;
        Vec3d pos = player.getEntityPos();
        SoundEvent sound = SoundEvents.INTENTIONALLY_EMPTY;

        if (!stack.isEmpty()) {
            ItemTransformData data = ItemTransformResourceReloadListener.getTransform(Registries.ITEM.getId(stack.getItem()));
            switch (soundSelector) {
                case 1 -> sound = Registries.SOUND_EVENT.get(data.sheatheId());
                case 2 -> sound = Registries.SOUND_EVENT.get(data.unsheatheId());
            }
        }

        player.getEntityWorld().playSoundClient(pos.getX(), pos.getY(), pos.getZ(), sound, SoundCategory.PLAYERS, CAConfig.backslotSwapSoundVolume / 100F, 1, true);
    }
}
