package net.hollowed.combatamenities.util.interfaces;

import net.minecraft.world.entity.player.Player;

public interface PlayerEntityRenderStateAccess {
    void combat_Amenities$setPlayerEntity(Player player);
    Player combat_Amenities$getPlayerEntity();
}
