package net.hollowed.combatamenities.mixin.slots.registration;

import net.hollowed.combatamenities.config.CAConfig;
import net.hollowed.combatamenities.util.json.ItemTransformData;
import net.hollowed.combatamenities.util.json.ItemTransformResourceReloadListener;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
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

        int xPos1 = 77;
        int yPos1 = 26;

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

            @Override
            public void setStack(ItemStack stack) {
                playSound(owner, stack);
                super.setStack(stack);
            }
        });

        // Add Belt Slot at determined position
        this.addSlot(new Slot(inventory, 42, xPos1, yPos1) {
            @Override
            public Identifier getBackgroundSprite() {
                return Identifier.ofVanilla("beltslot_overlay");
            }

            @Override
            public ItemStack takeStack(int amount) {
                ItemStack stack = super.takeStack(amount);
                if (stack.isEmpty()) {
                    this.setStack(ItemStack.EMPTY);
                }
                return stack;
            }

            @Override
            public void setStack(ItemStack stack) {
                playSound(owner, stack);
                super.setStack(stack);
            }
        });
    }

    @Unique
    private void playSound(LivingEntity entity, ItemStack stack) {
        ItemTransformData data = ItemTransformResourceReloadListener.getTransform(Registries.ITEM.getId(stack.getItem()));
        SoundEvent sound = Registries.SOUND_EVENT.get(data.sheatheId());

        entity.getEntityWorld().playSound(entity, entity.getX(), entity.getY(), entity.getZ(), sound, SoundCategory.PLAYERS, CAConfig.backslotSwapSoundVolume / 100F, 1, entity.getRandom().nextLong());
    }
}

