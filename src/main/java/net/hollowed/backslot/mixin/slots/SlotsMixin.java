package net.hollowed.backslot.mixin.slots;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerScreenHandler.class)
public abstract class SlotsMixin extends AbstractRecipeScreenHandler {

    protected SlotsMixin(ScreenHandlerType<?> screenHandlerType, int i) {
        super(screenHandlerType, i);
    }

    @Inject(method = "<init>(Lnet/minecraft/entity/player/PlayerInventory;ZLnet/minecraft/entity/player/PlayerEntity;)V", at = @At("RETURN"))
    private void addBackSlot(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo ci) {
        // Determine slot position based on context
        int xPos = 77;
        int yPos = 8;

        // Add BackSlot at determined position
        this.addSlot(new Slot(inventory, 41, xPos, yPos) {
            @Override
            public Identifier getBackgroundSprite() {
                return Identifier.ofVanilla("backslot_overlay");
            }

            @Override
            public ItemStack takeStack(int amount) {
                ItemStack stack = super.takeStack(amount);
                if (stack.isEmpty()) {
                    this.setStack(ItemStack.EMPTY);
                }
                return stack;
            }
        });
    }
}

