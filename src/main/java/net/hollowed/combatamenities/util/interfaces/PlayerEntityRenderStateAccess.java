package net.hollowed.combatamenities.util.interfaces;

import net.minecraft.entity.player.PlayerEntity;

public interface PlayerEntityRenderStateAccess {
    void combat_Amenities$setPlayerEntity(PlayerEntity player);
    PlayerEntity combat_Amenities$getPlayerEntity();
}
