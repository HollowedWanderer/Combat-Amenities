package net.hollowed.combatamenities.mixin.tweaks.arrow;

import net.hollowed.combatamenities.util.interfaces.ArrowEntityRenderStateAccess;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ArrowRenderState.class)
public class ArrowEntityRenderStateMixin implements ArrowEntityRenderStateAccess {

    @Unique
    private ItemStack itemStack;
    @Unique
    private Vec3 look;

    @Unique
    public void combat_Amenities$setItemStack(ItemStack stack) {
        itemStack = stack;
    }

    @Unique
    public void combat_Amenities$setLook(Vec3 lookdirection) {
        look = lookdirection;
    }

    @Unique
    public Vec3 combat_Amenities$getLook() {
        return look;
    }

    @Unique
    public ItemStack combat_Amenities$getItemStack() {
        return itemStack;
    }
}
