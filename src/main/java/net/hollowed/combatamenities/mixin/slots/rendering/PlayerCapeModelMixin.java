package net.hollowed.combatamenities.mixin.slots.rendering;

import net.hollowed.combatamenities.util.items.ModComponents;
import net.hollowed.combatamenities.util.json.BackTransformData;
import net.hollowed.combatamenities.util.json.BackTransformResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerCapeModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerCapeModel.class)
public class PlayerCapeModelMixin {

    @Shadow @Final private ModelPart cape;

    @Inject(method = "setAngles(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;)V", at = @At("TAIL"))
    private void injectSetAngles(PlayerEntityRenderState playerEntityRenderState, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            ItemStack stack = client.player.getInventory().getStack(41);
            BackTransformData transformData = BackTransformResourceReloadListener.getTransform(Registries.ITEM.getId(stack.getItem()), stack.getOrDefault(ModComponents.INTEGER_PROPERTY, -1).toString());
            float sway = 1.0F;
            if (!stack.isEmpty()) {
                sway = transformData.sway();
            }

            this.cape.rotate(new Quaternionf().rotateX(this.cape.pitch * sway).rotateY(this.cape.yaw).rotateZ(this.cape.roll));
        }
    }
}
