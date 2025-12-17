package net.hollowed.combatamenities.mixin.slots.registration;

import net.hollowed.combatamenities.config.CAConfig;
import net.hollowed.combatamenities.util.json.ItemTransformData;
import net.hollowed.combatamenities.util.json.ItemTransformResourceReloadListener;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class ScreenHandlerMixin {

    @Shadow @Final public NonNullList<Slot> slots;

    @Inject(method = "doClick", at = @At("HEAD"))
    private void internalOnSlotClick(int slotIndex, int button, ClickType actionType, Player player, CallbackInfo ci) {
        Inventory playerInventory = player.getInventory();
        if (actionType == ClickType.SWAP && (button == 41 || button == 42)) {
            if (button == 41 && slotIndex == 46 || button == 42 && slotIndex == 47) return;
            ItemStack itemStack5 = playerInventory.getItem(button);
            Slot slot = this.slots.get(slotIndex);
            ItemStack itemStack = slot.getItem();
            if (!itemStack5.isEmpty() || !itemStack.isEmpty()) {
                if (itemStack5.isEmpty()) {
                    if (slot.mayPickup(player)) {
                        playerInventory.setItem(button, itemStack);
                        slot.setByPlayer(ItemStack.EMPTY);
                        slot.onTake(player, itemStack);

                        playSound(player, itemStack, 1);
                    }
                } else if (itemStack.isEmpty()) {
                    if (slot.mayPlace(itemStack5)) {
                        int p = slot.getMaxStackSize(itemStack5);
                        if (itemStack5.getCount() > p) {
                            slot.setByPlayer(itemStack5.split(p));
                        } else {
                            playerInventory.setItem(button, ItemStack.EMPTY);
                            slot.setByPlayer(itemStack5);

                            playSound(player, itemStack5, 2);
                        }
                    }
                } else if (slot.mayPickup(player) && slot.mayPlace(itemStack5)) {
                    int p = slot.getMaxStackSize(itemStack5);
                    if (itemStack5.getCount() > p) {
                        slot.setByPlayer(itemStack5.split(p));
                        slot.onTake(player, itemStack);
                        if (!playerInventory.add(itemStack)) {
                            player.drop(itemStack, true);
                        }
                    } else {
                        playerInventory.setItem(button, itemStack);
                        slot.setByPlayer(itemStack5);
                        slot.onTake(player, itemStack);

                        playSound(player, itemStack, 1);
                        playSound(player, itemStack5, 2);
                    }
                }
            }
        }
    }

    @Unique
    private void playSound(Player player, ItemStack stack, int soundSelector) {
        if (player == null || !player.level().isClientSide()) return;
        Vec3 pos = player.position();
        SoundEvent sound = SoundEvents.EMPTY;

        if (!stack.isEmpty()) {
            ItemTransformData data = ItemTransformResourceReloadListener.getTransform(BuiltInRegistries.ITEM.getKey(stack.getItem()));
            switch (soundSelector) {
                case 1 -> sound = BuiltInRegistries.SOUND_EVENT.getValue(data.sheatheId());
                case 2 -> sound = BuiltInRegistries.SOUND_EVENT.getValue(data.unsheatheId());
            }
        }

        if (sound != null) {
            player.level().playLocalSound(pos.x(), pos.y(), pos.z(), sound, SoundSource.PLAYERS, CAConfig.backslotSwapSoundVolume / 100F, 1, true);
        }
    }
}
