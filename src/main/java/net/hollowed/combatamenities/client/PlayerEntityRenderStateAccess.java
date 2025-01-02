package net.hollowed.combatamenities.client;

import net.minecraft.entity.player.PlayerEntity;

public interface PlayerEntityRenderStateAccess {
    void combat_Amenities$setPlayerEntity(PlayerEntity player);
    PlayerEntity combat_Amenities$getPlayerEntity();
}
