package net.hollowed.combatamenities.mixin.slots.registration;

import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Environment(EnvType.CLIENT)
@Mixin(CreativeModeInventoryScreen.class)
public abstract class CACreativeInventoryScreenMixin extends AbstractContainerScreen<CreativeModeInventoryScreen.@NotNull ItemPickerMenu> {

    @Unique
    private static final Identifier SLOT_TEXTURE = Identifier.parse("textures/gui/sprites/container/slot.png");

    @Shadow private static CreativeModeTab selectedTab;

    @Shadow public abstract boolean isInventoryOpen();

    public CACreativeInventoryScreenMixin(CreativeModeInventoryScreen.ItemPickerMenu handler, Inventory inventory, Component title) {
        super(handler, inventory, title);
    }

    @ModifyArgs(
            method = "selectTab",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen$SlotWrapper;<init>(Lnet/minecraft/world/inventory/Slot;III)V"
            )
    )
    private void setSelectedTabMixin(
            Args args,
            @Local(ordinal = 0) int i
    ) {
        if (i == 46) {
            args.set(2, 127);
            args.set(3, 20);
        } else if (i == 47) {
            args.set(2, 145);
            args.set(3, 20);
        }
    }

    @Inject(method = "extractBackground", at = @At("TAIL"))
    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (this.isInventoryOpen()) {
            graphics.blit(
                    RenderPipelines.GUI_OPAQUE_TEXTURED_BACKGROUND,
                    SLOT_TEXTURE,
                    this.leftPos + 126, this.topPos + 19,
                    0, 0, 18, 18, 18, 18
            );
            graphics.blit(
                    RenderPipelines.GUI_OPAQUE_TEXTURED_BACKGROUND,
                    SLOT_TEXTURE,
                    this.leftPos + 144, this.topPos + 19,
                    0, 0, 18, 18, 18, 18
            );
        }
    }

    /*
    @Inject(method = "slotClicked", at = @At("TAIL"))
    private void onSlotClickMixin(Slot slot, int slotId, int buttonNum, ContainerInput containerInput, CallbackInfo ci) {
        CreativeModeTab inventoryGroup = BuiltInRegistries.CREATIVE_MODE_TAB.getValue(CreativeModeTabs.INVENTORY);

        if (selectedTab.equals(inventoryGroup)) {
            for (int i = 0; i < this.menu.slots.size(); ++i) {
                if (i == 46) {
                    ClientPlayNetworking.send(new SlotCreativeClientPacketPayload(EquipmentSlot.COMBATAMENITIES_BACKSLOT, this.menu.slots.get(i).getItem()));
                }
                if (i == 47) {
                    ClientPlayNetworking.send(new SlotCreativeClientPacketPayload(EquipmentSlot.COMBATAMENITIES_BELTSLOT, this.menu.slots.get(i).getItem()));
                }
            }
        }
    }
     */
}
