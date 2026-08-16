package net.hollowed.combatamenities.mixin.slots.networking;

import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Expression("45")
    @ModifyExpressionValue(
            method = "handleSetCreativeModeSlot",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private int handleSetCreativeModeSlot(int original) {
        return Math.max(original, 47);
    }
}
