package net.hollowed.combatamenities.mixin.slots.registration;

import net.hollowed.combatamenities.config.CAConfig;
import net.hollowed.combatamenities.util.json.ItemTransformData;
import net.hollowed.combatamenities.util.json.ItemTransformResourceReloadListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryMenu.class)
public abstract class SlotsMixin extends RecipeBookMenu {

    protected SlotsMixin(MenuType<?> screenHandlerType, int i) {
        super(screenHandlerType, i);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/entity/player/Inventory;ZLnet/minecraft/world/entity/player/Player;)V", at = @At("RETURN"))
    private void addBackSlot(Inventory inventory, boolean onServer, Player owner, CallbackInfo ci) {
        // Determine slot position based on context
        int xPos = 77;
        int yPos = 8;

        int xPos1 = 77;
        int yPos1 = 26;

        // Add BackSlot at determined position
        this.addSlot(new Slot(inventory, 41, xPos, yPos) {
            @Override
            public Identifier getNoItemIcon() {
                return Identifier.withDefaultNamespace("backslot_overlay");
            }

            @Override
            public @NotNull ItemStack remove(int amount) {
                ItemStack stack = super.remove(amount);
                if (stack.isEmpty()) {
                    this.setByPlayer(ItemStack.EMPTY);
                }
                return stack;
            }

            @Override
            public void setByPlayer(@NotNull ItemStack stack) {
                playSound(owner, stack);
                super.setByPlayer(stack);
            }
        });

        // Add Belt Slot at determined position
        this.addSlot(new Slot(inventory, 42, xPos1, yPos1) {
            @Override
            public Identifier getNoItemIcon() {
                return Identifier.withDefaultNamespace("beltslot_overlay");
            }

            @Override
            public @NotNull ItemStack remove(int amount) {
                ItemStack stack = super.remove(amount);
                if (stack.isEmpty()) {
                    this.setByPlayer(ItemStack.EMPTY);
                }
                return stack;
            }

            @Override
            public void setByPlayer(@NotNull ItemStack stack) {
                playSound(owner, stack);
                super.setByPlayer(stack);
            }
        });
    }

    @Unique
    private void playSound(LivingEntity entity, ItemStack stack) {
        ItemTransformData data = ItemTransformResourceReloadListener.getTransform(BuiltInRegistries.ITEM.getKey(stack.getItem()));
        SoundEvent sound = BuiltInRegistries.SOUND_EVENT.getValue(data.sheatheId());

        assert sound != null;
        entity.level().playSeededSound(entity, entity.getX(), entity.getY(), entity.getZ(), sound, SoundSource.PLAYERS, CAConfig.backslotSwapSoundVolume / 100F, 1, entity.getRandom().nextLong());
    }
}

