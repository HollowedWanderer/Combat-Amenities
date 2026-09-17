package net.hollowed.combatamenities.mixin.tweaks.bow;

import com.mojang.blaze3d.vertex.PoseStack;
import net.hollowed.combatamenities.config.CAConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStackRenderState.class)
public class FirstPersonItemMixin {

    @Shadow
    ItemDisplayContext displayContext;

    @Inject(method = "submit",
            at = @At("HEAD"), cancellable = true)
    private void hideOffhandItemWhenUsingRiptide(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, int overlayCoords, int outlineColor, CallbackInfo ci) {
        AbstractClientPlayer entity = Minecraft.getInstance().player;
        if (entity == null) return;
        if (entity.isAutoSpinAttack()) {
            HumanoidArm offhandArm = entity.getMainArm() == HumanoidArm.RIGHT ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
            if (entity.getUsedItemHand() == InteractionHand.MAIN_HAND) {
                if (((offhandArm == HumanoidArm.LEFT && displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                        || (offhandArm == HumanoidArm.RIGHT && displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND))
                        && CAConfig.riptideFix) {
                    ci.cancel();
                }
            } else {
                if (((offhandArm == HumanoidArm.RIGHT && displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                        || (offhandArm == HumanoidArm.LEFT && displayContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND))
                        && CAConfig.riptideFix) {
                    ci.cancel();
                }
            }
        }

        if (entity.getUseItem().getItem() instanceof BowItem && CAConfig.bowTweaks && displayContext.firstPerson()) {
            int useTime = entity.getTicksUsingItem();

            float tickDelta = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);

            if (useTime > 120) {
                float wobbleStrength = Math.min((useTime - 120) / 25.0F, 0.25F);
                float wobbleAmount = (float) Math.sin(useTime + tickDelta) * 0.1F * wobbleStrength;
                poseStack.translate(0.0F, wobbleAmount, 0.0F);
            }
        }
    }
}