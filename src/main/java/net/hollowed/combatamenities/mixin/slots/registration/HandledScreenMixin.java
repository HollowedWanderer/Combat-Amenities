package net.hollowed.combatamenities.mixin.slots.registration;

import net.hollowed.combatamenities.config.CAConfig;
import net.hollowed.combatamenities.data.read.ItemTransformData;
import net.hollowed.combatamenities.data.read.ItemTransformResourceReloadListener;
import net.hollowed.combatamenities.index.CAKeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class HandledScreenMixin<T extends AbstractContainerMenu> {

    @Shadow @Nullable protected Slot hoveredSlot;

    @Shadow @Final protected T menu;

    @Shadow
    protected abstract void slotClicked(Slot slot, int slotId, int buttonNum, ContainerInput containerInput);

    @Inject(method = "checkHotbarMouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;)V", at = @At("HEAD"), cancellable = true)
    private void swapBackOrBeltSlotMouse(MouseButtonEvent click, CallbackInfo ci) {
        if (this.hoveredSlot != null && this.menu.getCarried().isEmpty()) {
            if (CAKeyBindings.backSlotBinding.matchesMouse(click) && hoveredSlot.index != 46) {
                playSound(Minecraft.getInstance().player, hoveredSlot.getItem(), menu.slots.get(46).getItem());
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 46, ContainerInput.SWAP);
                ci.cancel();
                return;
            }

            if (CAKeyBindings.beltSlotBinding.matchesMouse(click) && hoveredSlot.index != 47) {
                playSound(Minecraft.getInstance().player, hoveredSlot.getItem(), menu.slots.get(47).getItem());
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 47, ContainerInput.SWAP);
                ci.cancel();
            }
        }
    }

    @Inject(method = "checkHotbarKeyPressed", at = @At("HEAD"), cancellable = true)
    private void swapBackOrBeltSlot(KeyEvent keyInput, CallbackInfoReturnable<Boolean> cir) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null) {
            if (CAKeyBindings.backSlotBinding.matches(keyInput) && hoveredSlot.index != 46) {
                playSound(Minecraft.getInstance().player, hoveredSlot.getItem(), menu.slots.get(46).getItem());
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 46, ContainerInput.SWAP);
                cir.setReturnValue(true);
                return;
            }

            if (CAKeyBindings.beltSlotBinding.matches(keyInput) && hoveredSlot.index != 47) {
                playSound(Minecraft.getInstance().player, hoveredSlot.getItem(), menu.slots.get(47).getItem());
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 47, ContainerInput.SWAP);
                cir.setReturnValue(true);
            }
        }
    }

    @Unique
    private void playSound(LivingEntity entity, ItemStack stack, ItemStack other) {
        if (stack.isEmpty() && other.isEmpty()) {
            return;
        }

        ItemTransformData data = ItemTransformResourceReloadListener.getTransform(BuiltInRegistries.ITEM.getKey(stack.getItem()));
        SoundEvent sound = BuiltInRegistries.SOUND_EVENT.getValue(data.sheatheId());

        assert sound != null;
        entity.level().playSeededSound(entity, entity.getX(), entity.getY(), entity.getZ(), sound, SoundSource.PLAYERS, CAConfig.backslotSwapSoundVolume / 100F, 1, entity.getRandom().nextLong());
    }
}
