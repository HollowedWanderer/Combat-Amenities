package net.hollowed.combatamenities.mixin.tweaks.trident;

import net.hollowed.combatamenities.util.interfaces.TridentEntityRenderStateAccess;
import net.minecraft.client.render.entity.state.TridentEntityRenderState;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TridentEntityRenderState.class)
public class TridentEntityRenderStateMixin implements TridentEntityRenderStateAccess {
    @Unique
    private static Vec3d look;

    @Override
    public void combat_Amenities$setLook(Vec3d lookDirection) {
        look = lookDirection;
    }

    @Override
    public Vec3d combat_Amenities$getLook() {
        return look;
    }
}
