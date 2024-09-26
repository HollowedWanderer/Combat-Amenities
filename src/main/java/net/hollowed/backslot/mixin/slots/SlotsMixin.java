package net.hollowed.backslot.mixin.slots;

import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.RecipeInputInventory;
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
public abstract class SlotsMixin extends AbstractRecipeScreenHandler<RecipeInputInventory> {

    protected SlotsMixin(ScreenHandlerType<?> screenHandlerType, int i) {
        super(screenHandlerType, i);
    }

    @Inject(method = "<init>(Lnet/minecraft/entity/player/PlayerInventory;ZLnet/minecraft/entity/player/PlayerEntity;)V", at = @At("RETURN"))
    private void addBackSlot(PlayerInventory inventory, boolean onServer, PlayerEntity owner, CallbackInfo ci) {
            // Add BackSlot at (77, 8)
            this.addSlot(new Slot(inventory, 41, 77, 8) {
                @Override
                public Pair<Identifier, Identifier> getBackgroundSprite() {
                    return new Pair<>(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier("item/back_slot_overlay"));
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
