package net.hollowed.combatamenities.mixin.slots.rendering;

import net.hollowed.combatamenities.util.interfaces.PlayerEntityRenderStateAccess;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntityRenderState.class)
public class PlayerRenderStateMixin implements PlayerEntityRenderStateAccess {
    @Unique
    private PlayerEntity playerEntity;

    @Override
    public void combat_Amenities$setPlayerEntity(PlayerEntity player) {
        this.playerEntity = player;
    }

    @Override
    public PlayerEntity combat_Amenities$getPlayerEntity() {
        return this.playerEntity;
    }
}

