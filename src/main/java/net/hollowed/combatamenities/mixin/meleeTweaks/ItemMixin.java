package net.hollowed.combatamenities.mixin.meleeTweaks;

import net.hollowed.combatamenities.util.interfaces.WeaponRework;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(Item.class)
public abstract class ItemMixin implements WeaponRework {

    @Shadow public abstract ItemStack getDefaultStack();

    @Override
    public int combat_Amenities$delay() {
        if (this.getDefaultStack().streamTags().toList().contains(TagKey.of(RegistryKeys.ITEM, Identifier.ofVanilla("swords")))) {
            return 2;
        }
        return 0;
    }

    @Override
    public List<Object> combat_Amenities$sound() {
        if (this.getDefaultStack().streamTags().toList().contains(TagKey.of(RegistryKeys.ITEM, Identifier.ofVanilla("swords")))) {
            return List.of(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
        return List.of(SoundEvents.INTENTIONALLY_EMPTY, SoundCategory.MASTER, 0.0F, 0.0F);
    }
}
