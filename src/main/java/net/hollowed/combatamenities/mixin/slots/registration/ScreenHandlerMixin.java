package net.hollowed.combatamenities.mixin.slots.registration;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.hollowed.combatamenities.config.CAConfig;
import net.hollowed.combatamenities.data.read.ItemTransformData;
import net.hollowed.combatamenities.data.read.ItemTransformResourceReloadListener;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractContainerMenu.class)
public abstract class ScreenHandlerMixin {

    @Shadow @Final public NonNullList<Slot> slots;

    @Definition(id = "buttonNum", local = @Local(argsOnly = true, ordinal = 1, type = int.class))
    @Expression("buttonNum == 40")
    @ModifyExpressionValue(
            method = "doClick",
            at = @At(
                    "MIXINEXTRAS:EXPRESSION"
            )
    )
    private boolean internalOnSlotClick(boolean original, @Local(argsOnly = true, ordinal = 1) int buttonNum) {
        return original || buttonNum == 46 || buttonNum == 47;
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
