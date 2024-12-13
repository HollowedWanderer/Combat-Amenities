package net.hollowed.backslot.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.player.PlayerEntity;

@Environment(EnvType.CLIENT)
public class ExtendedPlayerEntityRenderState extends PlayerEntityRenderState {
    private static PlayerEntity playerEntity;

    public static void setPlayerEntity(PlayerEntity player) {
        playerEntity = player;
    }

    // Get the player entity this render state is associated with
    public PlayerEntity getPlayerEntity() {
        return playerEntity;
    }
}