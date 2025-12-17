package net.hollowed.combatamenities.mixin.slots.registration;

import net.hollowed.combatamenities.index.CAKeyBindings;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public abstract class HandledScreenMixin<T extends AbstractContainerMenu> {

    @Shadow @Nullable protected Slot hoveredSlot;

    @Shadow @Final protected T menu;

    @Shadow protected abstract void slotClicked(Slot slot, int slotId, int button, ClickType actionType);

    @Inject(method = "checkHotbarMouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;)V", at = @At("HEAD"), cancellable = true)
    private void swapBackOrBeltSlotMouse(MouseButtonEvent click, CallbackInfo ci) {
        if (this.hoveredSlot != null && this.menu.getCarried().isEmpty()) {
            if (CAKeyBindings.backSlotBinding.matchesMouse(click)) {
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 41, ClickType.SWAP);
                ci.cancel();
                return;
            }

            if (CAKeyBindings.beltSlotBinding.matchesMouse(click)) {
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 42, ClickType.SWAP);
                ci.cancel();
            }
        }
    }

    @Inject(method = "checkHotbarKeyPressed", at = @At("HEAD"), cancellable = true)
    private void swapBackOrBeltSlot(KeyEvent keyInput, CallbackInfoReturnable<Boolean> cir) {
        if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null) {
            if (CAKeyBindings.backSlotBinding.matches(keyInput)) {
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 41, ClickType.SWAP);
                cir.setReturnValue(true);
                return;
            }

            if (CAKeyBindings.beltSlotBinding.matches(keyInput)) {
                this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 42, ClickType.SWAP);
                cir.setReturnValue(true);
            }
        }
    }
}
