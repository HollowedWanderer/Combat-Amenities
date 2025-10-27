package net.hollowed.combatamenities.mixin.slots.registration;

import net.hollowed.combatamenities.index.CAKeyBindings;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> {

    @Shadow @Nullable protected Slot focusedSlot;

    @Shadow @Final protected T handler;

    @Shadow protected abstract void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType);

    @Inject(method = "onMouseClick(Lnet/minecraft/client/gui/Click;)V", at = @At("HEAD"), cancellable = true)
    private void swapBackOrBeltSlotMouse(Click click, CallbackInfo ci) {
        if (this.focusedSlot != null && this.handler.getCursorStack().isEmpty()) {
            if (CAKeyBindings.backSlotBinding.matchesMouse(click)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 41, SlotActionType.SWAP);
                ci.cancel();
                return;
            }

            if (CAKeyBindings.beltSlotBinding.matchesMouse(click)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 42, SlotActionType.SWAP);
                ci.cancel();
            }
        }
    }

    @Inject(method = "handleHotbarKeyPressed", at = @At("HEAD"), cancellable = true)
    private void swapBackOrBeltSlot(KeyInput keyInput, CallbackInfoReturnable<Boolean> cir) {
        if (this.handler.getCursorStack().isEmpty() && this.focusedSlot != null) {
            if (CAKeyBindings.backSlotBinding.matchesKey(keyInput)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 41, SlotActionType.SWAP);
                cir.setReturnValue(true);
                return;
            }

            if (CAKeyBindings.beltSlotBinding.matchesKey(keyInput)) {
                this.onMouseClick(this.focusedSlot, this.focusedSlot.id, 42, SlotActionType.SWAP);
                cir.setReturnValue(true);
            }
        }
    }
}
