package net.hollowed.combatamenities.mixin.meleeTweaks;

import net.hollowed.combatamenities.util.WeaponRework;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.List;

@Mixin(Item.class)
public abstract class SwordFunctions implements WeaponRework {
    @Shadow public abstract ItemStack getDefaultStack();

    @Unique
    private static final TagKey<Item> SWORD_TAG = TagKey.of(Registries.ITEM.getKey(), Identifier.ofVanilla("swords"));

    @Override
    public int combat_Amenities$delay() {
        return 2;
    }

    @Override
    public List<Object> combat_Amenities$sound() {
        if (this.getDefaultStack().isIn(SWORD_TAG)) {
            return List.of(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }
        return List.of(SoundEvents.INTENTIONALLY_EMPTY, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }
}
