package net.hollowed.combatamenities.mixin.tweaks.bow;

import com.mojang.blaze3d.vertex.PoseStack;
import net.hollowed.combatamenities.config.CAConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class FirstPersonItemMixin {

    @Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;I)V",
            at = @At("HEAD"), cancellable = true)
    private void hideOffhandItemWhenUsingRiptide(LivingEntity entity, ItemStack stack, ItemDisplayContext renderMode, PoseStack matrices, SubmitNodeCollector orderedRenderCommandQueue, int light, CallbackInfo ci) {
        if (entity.isAutoSpinAttack()) {
            HumanoidArm offhandArm = entity.getMainArm() == HumanoidArm.RIGHT ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
            if (entity.getUsedItemHand() == InteractionHand.MAIN_HAND) {
                if (((offhandArm == HumanoidArm.LEFT && renderMode == ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                        || (offhandArm == HumanoidArm.RIGHT && renderMode == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND))
                        && CAConfig.riptideFix) {
                    ci.cancel();
                }
            } else {
                if (((offhandArm == HumanoidArm.RIGHT && renderMode == ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                        || (offhandArm == HumanoidArm.LEFT && renderMode == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND))
                        && CAConfig.riptideFix) {
                    ci.cancel();
                }
            }
        }

        if (entity instanceof Player player && player.getUseItem().getItem() instanceof BowItem && CAConfig.bowTweaks) {
            int useTime = player.getTicksUsingItem();

            float tickDelta = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);

            if (useTime > 120) {
                float wobbleStrength = Math.min((useTime - 120) / 25.0F, 0.25F);
                float wobbleAmount = (float) Math.sin(useTime + tickDelta) * 0.1F * wobbleStrength;
                matrices.translate(0.0F, wobbleAmount, 0.0F);
            }
        }
    }
}