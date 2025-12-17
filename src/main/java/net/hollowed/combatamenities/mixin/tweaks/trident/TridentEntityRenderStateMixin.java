package net.hollowed.combatamenities.mixin.tweaks.trident;

import net.hollowed.combatamenities.util.interfaces.TridentEntityRenderStateAccess;
import net.minecraft.client.renderer.entity.state.ThrownTridentRenderState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ThrownTridentRenderState.class)
public class TridentEntityRenderStateMixin implements TridentEntityRenderStateAccess {
    @Unique
    private static Vec3 look;

    @Override
    public void combat_Amenities$setLook(Vec3 lookDirection) {
        look = lookDirection;
    }

    @Override
    public Vec3 combat_Amenities$getLook() {
        return look;
    }
}
