package net.hollowed.combatamenities.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.ArrowEntityRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ExtendedArrowEntityRenderState extends ArrowEntityRenderState {
    private static ItemStack itemStack;
    private static Vec3d look;

    public void setItemStack(ItemStack stack) {
        itemStack = stack;
    }

    public void setLook(Vec3d lookdirection) {
        look = lookdirection;
    }

    public Vec3d getLook() {
        return look;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
}