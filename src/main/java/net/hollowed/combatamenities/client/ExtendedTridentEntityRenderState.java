package net.hollowed.combatamenities.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.TridentEntityRenderState;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ExtendedTridentEntityRenderState extends TridentEntityRenderState {
    private static Vec3d look;

    public void setLook(Vec3d lookdirection) {
        look = lookdirection;
    }

    public Vec3d getLook() {
        return look;
    }
}