package net.hollowed.combatamenities.mixin;

import net.hollowed.combatamenities.networking.KeybindEventHandler;
import net.hollowed.combatamenities.util.delay.ClientTickDelayScheduler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onClientInit(CallbackInfo ci) {
        KeybindEventHandler.register();
    }

    @Inject(method = "setLevel", at = @At("TAIL"))
    public void joinWorld(ClientLevel world, CallbackInfo ci) {
        ClientTickDelayScheduler.run = true;
    }
}
