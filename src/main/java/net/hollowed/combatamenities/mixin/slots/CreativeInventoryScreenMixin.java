package net.hollowed.combatamenities.mixin.slots;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.hollowed.combatamenities.networking.BackSlotCreativeClientPacketPayload;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Environment(EnvType.CLIENT)
@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin extends HandledScreen<CreativeInventoryScreen.CreativeScreenHandler> {

    @Shadow private static ItemGroup selectedTab;

    public CreativeInventoryScreenMixin(CreativeInventoryScreen.CreativeScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "setSelectedTab", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/ingame/CreativeInventoryScreen;deleteItemSlot:Lnet/minecraft/screen/slot/Slot;", shift = At.Shift.BEFORE))
    private void setSelectedTabMixin(ItemGroup group, CallbackInfo info) {
        for (int i = 0; i < this.handler.slots.size(); ++i) {
            if (i == 46) {  // Modify slot 46
                Slot slot = this.handler.slots.get(i);

                // Use the accessor to modify the final x and y fields
                if (slot instanceof SlotAccessor accessor) {
                    accessor.setX(127);  // Update X position
                    accessor.setY(20);   // Update Y position
                }
            }
        }
    }


    @Inject(method = "onMouseClick", at = @At("TAIL"))
    private void onSlotClickMixin(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        ItemGroup inventoryGroup = Registries.ITEM_GROUP.get(ItemGroups.INVENTORY);

        if (selectedTab.equals(inventoryGroup)) {
            for (int i = 0; i < this.handler.slots.size(); ++i) {
                if (i == 46) {  // Modify slot 46
                    assert MinecraftClient.getInstance().player != null;
                    ClientPlayNetworking.send(new BackSlotCreativeClientPacketPayload(MinecraftClient.getInstance().player.getId(), 41, this.handler.slots.get(i).getStack()));
                }
            }
        }
    }
}
