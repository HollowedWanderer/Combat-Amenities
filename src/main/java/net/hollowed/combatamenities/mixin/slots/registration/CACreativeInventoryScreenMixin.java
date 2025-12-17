package net.hollowed.combatamenities.mixin.slots.registration;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.hollowed.combatamenities.networking.slots.SlotCreativeClientPacketPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @SuppressWarnings("all")
    @Inject(method = "selectTab", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/inventory/CreativeModeInventoryScreen;destroyItemSlot:Lnet/minecraft/world/inventory/Slot;", shift = At.Shift.BEFORE))
    private void setSelectedTabMixin(CreativeModeTab group, CallbackInfo info) {
        for (int i = 0; i < this.menu.slots.size(); ++i) {
            if (i == 46) {  // Modify slot 46
                Slot slot = this.menu.slots.get(i);

                // Use the accessor to modify the final x and y fields
                if (slot instanceof SlotAccessor accessor) {
                    accessor.setX(127);  // Update X position
                    accessor.setY(20);   // Update Y position
                }
            }
            if (i == 47) {  // Modify slot 46
                Slot slot = this.menu.slots.get(i);

                // Use the accessor to modify the final x and y fields
                if (slot instanceof SlotAccessor accessor) {
                    accessor.setX(145);  // Update X position
                    accessor.setY(20);   // Update Y position
                }
            }
        }
    }

    @Inject(method = "renderBg", at = @At("TAIL"))
    public void render(GuiGraphics context, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.isInventoryOpen()) {
            context.blit(
                    RenderPipelines.GUI_OPAQUE_TEXTURED_BACKGROUND,
                    SLOT_TEXTURE,
                    this.leftPos + 126, this.topPos + 19,
                    0, 0, 18, 18, 18, 18 // Texture coordinates and dimensions
            );
            context.blit(
                    RenderPipelines.GUI_OPAQUE_TEXTURED_BACKGROUND,
                    SLOT_TEXTURE,
                    this.leftPos + 144, this.topPos + 19,
                    0, 0, 18, 18, 18, 18 // Texture coordinates and dimensions
            );
        }
    }

    @Inject(method = "slotClicked", at = @At("TAIL"))
    private void onSlotClickMixin(Slot slot, int slotId, int button, ClickType actionType, CallbackInfo ci) {
        CreativeModeTab inventoryGroup = BuiltInRegistries.CREATIVE_MODE_TAB.getValue(CreativeModeTabs.INVENTORY);

        if (selectedTab.equals(inventoryGroup)) {
            for (int i = 0; i < this.menu.slots.size(); ++i) {
                if (i == 46) {  // Modify slot 46
                    ClientPlayNetworking.send(new SlotCreativeClientPacketPayload(41, this.menu.slots.get(i).getItem()));
                }
                if (i == 47) {  // Modify slot 47
                    ClientPlayNetworking.send(new SlotCreativeClientPacketPayload(42, this.menu.slots.get(i).getItem()));
                }
            }
        }
    }
}
