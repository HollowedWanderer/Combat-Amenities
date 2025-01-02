package net.hollowed.combatamenities.mixin;

import net.hollowed.combatamenities.client.ArrowEntityRenderStateAccess;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ProjectileEntityRenderState.class)
public class ArrowEntityRenderStateMixin implements ArrowEntityRenderStateAccess {
    @Unique
    private static ItemStack itemStack;
    @Unique
    private static Vec3d look;

    @Unique
    public void combat_Amenities$setItemStack(ItemStack stack) {
        itemStack = stack;
    }

    @Unique
    public void combat_Amenities$setLook(Vec3d lookdirection) {
        look = lookdirection;
    }

    @Unique
    public Vec3d combat_Amenities$getLook() {
        return look;
    }

    @Unique
    public ItemStack combat_Amenities$getItemStack() {
        return itemStack;
    }
}
