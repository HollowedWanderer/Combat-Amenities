package net.hollowed.combatamenities.mixin.tweaks.trident;

import net.hollowed.combatamenities.util.interfaces.TridentEntityRenderStateAccess;
import net.minecraft.client.renderer.entity.state.ThrownTridentRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ThrownTridentRenderState.class)
public class TridentEntityRenderStateMixin implements TridentEntityRenderStateAccess {
    @Unique
    private Entity look;

    @Override
    public void combat_Amenities$setEntity(Entity lookDirection) {
        look = lookDirection;
    }

    @Override
    public Entity combat_Amenities$getEntity() {
        return look;
    }
}
