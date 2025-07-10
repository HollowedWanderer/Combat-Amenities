package net.hollowed.combatamenities.mixin.tweaks.arrow;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.combatamenities.config.ModConfig;
import net.hollowed.combatamenities.util.interfaces.ArrowEntityRenderStateAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

@Mixin(ProjectileEntityRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class ProjectileEntityRendererMixin<T extends PersistentProjectileEntity> {

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/projectile/PersistentProjectileEntity;Lnet/minecraft/client/render/entity/state/ProjectileEntityRenderState;F)V",
            at = @At("HEAD"))
    public void updateRenderState(T persistentProjectileEntity, ProjectileEntityRenderState projectileEntityRenderState, float f, CallbackInfo ci) {

        if (ModConfig.itemArrows) {
            if (projectileEntityRenderState instanceof ArrowEntityRenderStateAccess access) {

                ItemStack stack = persistentProjectileEntity.getItemStack();
                Vec3d look = new Vec3d(0, 0, 0);

                // Default item stack to what the entity provides
                if (persistentProjectileEntity instanceof ArrowEntity arrowEntity) {
                    if (arrowEntity.getColor() != -1) {
                        stack = new ItemStack(Items.TIPPED_ARROW);
                        stack.set(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(Optional.empty(), Optional.of(arrowEntity.getColor()), List.of(), Optional.empty()));
                    }
                    look = arrowEntity.getRotationVec(0);
                }

                // Pass the dynamically constructed item stack
                access.combat_Amenities$setItemStack(stack);
                access.combat_Amenities$setLook(look);
            }
        }
    }

    @Inject(
        method = "render(Lnet/minecraft/client/render/entity/state/ProjectileEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void renderWithItem(
            ProjectileEntityRenderState projectileEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci  // Use Object because the generic types are not accessible directly
    ) {
        if (ModConfig.itemArrows) {
            if (projectileEntityRenderState instanceof ArrowEntityRenderStateAccess access) {
                // Push the matrix stack for transformations
                matrixStack.push();

                // Create the item stack (e.g., an arrow item)
                ItemStack itemStack = access.combat_Amenities$getItemStack();

                float multiplier = 0.25F;

                // Apply translation
                matrixStack.translate(access.combat_Amenities$getLook().multiply(multiplier, multiplier, -multiplier));

                // Apply rotations for the projectile's orientation
                matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(projectileEntityRenderState.yaw - 90.0F));
                matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(projectileEntityRenderState.pitch - 45.0F));

                // Render the item using the ItemRenderer
                ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
                itemRenderer.renderItem(
                        itemStack,
                        ItemDisplayContext.NONE,
                        light,
                        OverlayTexture.DEFAULT_UV,
                        matrixStack,
                        vertexConsumerProvider,
                        MinecraftClient.getInstance().world,
                        0
                );

                // Pop the matrix stack
                matrixStack.pop();

                // Cancel the original rendering
                ci.cancel();
            }
        }
    }
}
