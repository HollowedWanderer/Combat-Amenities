package net.hollowed.combatamenities.mixin.slots.rendering;

import net.hollowed.combatamenities.util.interfaces.PlayerEntityRenderStateAccess;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class PlayerRenderStateMixin implements PlayerEntityRenderStateAccess {
    @Unique
    private Player playerEntity;

    @Override
    public void combat_Amenities$setPlayerEntity(Player player) {
        this.playerEntity = player;
    }

    @Override
    public Player combat_Amenities$getPlayerEntity() {
        return this.playerEntity;
    }
}

