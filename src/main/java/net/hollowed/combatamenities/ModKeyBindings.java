package net.hollowed.combatamenities;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.hollowed.combatamenities.mixin.slots.HandledScreenAccessor;
import net.hollowed.combatamenities.networking.BackSlotInventoryPacketPayload;
import net.hollowed.combatamenities.networking.BeltSlotInventoryPacketPayload;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {

    // Keybinding
    public static KeyBinding backSlotBinding;
    public static KeyBinding beltSlotBinding;

    public static void initialize() {
        registerKeyBindings();
        setupInventoryKeyDetection();
    }

    private static void registerKeyBindings() {
        backSlotBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.combatamenities.backslot",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.combatamenities.keybinds"
        ));
        beltSlotBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.combatamenities.beltslot",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "category.combatamenities.keybinds"
        ));
    }

    private static void setupInventoryKeyDetection() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (MinecraftClient.getInstance().currentScreen != null) {
                handleInventoryKeyPress();
            }
        });
    }

    private static void handleInventoryKeyPress() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.currentScreen == null) {
            return;
        }

        // Check if the keybinding's key is pressed
        long windowHandle = MinecraftClient.getInstance().getWindow().getHandle();
        InputUtil.Key boundKey;
        InputUtil.Key boundKey1;
        if (KeyBindingHelper.getBoundKeyOf(ModKeyBindings.backSlotBinding).toInt().isEmpty()) {
            boundKey = ModKeyBindings.backSlotBinding.getDefaultKey();
        } else {
            boundKey = KeyBindingHelper.getBoundKeyOf(ModKeyBindings.backSlotBinding);
        }
        if (KeyBindingHelper.getBoundKeyOf(ModKeyBindings.beltSlotBinding).toInt().isEmpty()) {
            boundKey1 = ModKeyBindings.beltSlotBinding.getDefaultKey();
        } else {
            boundKey1 = KeyBindingHelper.getBoundKeyOf(ModKeyBindings.beltSlotBinding);
        }
        if (InputUtil.isKeyPressed(windowHandle, boundKey.getCode())) {
            handleBackSlotSwap(client);
        } else if (InputUtil.isKeyPressed(windowHandle, boundKey1.getCode())) {
            handleBeltSlotSwap(client);
        }
    }

    // Global variable to track the last time the key was pressed
    private static long lastSwapTime = 0;  // Time of last swap in milliseconds
    private static final long COOLDOWN_TIME = 500;  // Cooldown time in milliseconds (500 ms = 0.5 seconds)

    private static void handleBackSlotSwap(MinecraftClient client) {
        if (client.player == null) {
            return;
        }

        // Check if the current screen is an instance of CreativeInventoryScreen
        if (client.currentScreen instanceof CreativeInventoryScreen creativeScreen) {

            if (!creativeScreen.isInventoryTabSelected()) {
                return; // If not in the "Inventory" tab, do nothing
            }

            // Ensure that the cooldown has passed before proceeding
            long currentTime = System.currentTimeMillis(); // Get current time in milliseconds

            if (currentTime - lastSwapTime < COOLDOWN_TIME) {
                return; // Do nothing if the cooldown has not passed
            }

            double scaleFactor = client.getWindow().getScaleFactor(); // Get scale factor of the window
            double mouseX = client.mouse.getX() / scaleFactor; // Apply scaling
            double mouseY = client.mouse.getY() / scaleFactor; // Apply scaling

            try {
                HandledScreenAccessor accessor = (HandledScreenAccessor) creativeScreen;

                Slot hoveredSlot = accessor.invokeGetSlotAt(mouseX, mouseY);
                if (hoveredSlot == null) {
                    return;
                }

                ItemStack hoveredSlotStack = hoveredSlot.getStack();

                // Send packet
                BackSlotInventoryPacketPayload payload = new BackSlotInventoryPacketPayload(hoveredSlotStack, hoveredSlot.getIndex());
                ClientPlayNetworking.send(payload);

                lastSwapTime = System.currentTimeMillis();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (client.currentScreen instanceof InventoryScreen inventoryScreen) {

            long currentTime = System.currentTimeMillis(); // Get current time in milliseconds

            // Check if the cooldown period has passed
            if (currentTime - lastSwapTime < COOLDOWN_TIME) {
                return; // Do nothing if the cooldown has not passed
            }

            double scaleFactor = client.getWindow().getScaleFactor(); // Get scale factor of the window
            double mouseX = client.mouse.getX() / scaleFactor; // Apply scaling
            double mouseY = client.mouse.getY() / scaleFactor; // Apply scaling

            try {
                HandledScreenAccessor accessor = (HandledScreenAccessor) inventoryScreen;

                Slot hoveredSlot = accessor.invokeGetSlotAt(mouseX, mouseY);
                if (hoveredSlot == null) {
                    return;
                }

                ItemStack hoveredSlotStack = hoveredSlot.getStack();

                // Send packet
                BackSlotInventoryPacketPayload payload = new BackSlotInventoryPacketPayload(hoveredSlotStack, hoveredSlot.getIndex());
                ClientPlayNetworking.send(payload);

                lastSwapTime = System.currentTimeMillis();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Global variable to track the last time the key was pressed
    private static long lastSwapTime1 = 0;  // Time of last swap in milliseconds

    private static void handleBeltSlotSwap(MinecraftClient client) {
        if (client.player == null) {
            return;
        }

        // Check if the current screen is an instance of CreativeInventoryScreen
        if (client.currentScreen instanceof CreativeInventoryScreen creativeScreen) {

            if (!creativeScreen.isInventoryTabSelected()) {
                return; // If not in the "Inventory" tab, do nothing
            }

            // Ensure that the cooldown has passed before proceeding
            long currentTime = System.currentTimeMillis(); // Get current time in milliseconds

            if (currentTime - lastSwapTime1 < COOLDOWN_TIME) {
                return; // Do nothing if the cooldown has not passed
            }

            double scaleFactor = client.getWindow().getScaleFactor(); // Get scale factor of the window
            double mouseX = client.mouse.getX() / scaleFactor; // Apply scaling
            double mouseY = client.mouse.getY() / scaleFactor; // Apply scaling

            try {
                HandledScreenAccessor accessor = (HandledScreenAccessor) creativeScreen;

                Slot hoveredSlot = accessor.invokeGetSlotAt(mouseX, mouseY);
                if (hoveredSlot == null) {
                    return;
                }

                ItemStack hoveredSlotStack = hoveredSlot.getStack();

                // Send packet
                BeltSlotInventoryPacketPayload payload = new BeltSlotInventoryPacketPayload(hoveredSlotStack, hoveredSlot.getIndex());
                ClientPlayNetworking.send(payload);

                lastSwapTime1 = System.currentTimeMillis();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (client.currentScreen instanceof InventoryScreen inventoryScreen) {

            long currentTime = System.currentTimeMillis(); // Get current time in milliseconds

            // Check if the cooldown period has passed
            if (currentTime - lastSwapTime1 < COOLDOWN_TIME) {
                return; // Do nothing if the cooldown has not passed
            }

            double scaleFactor = client.getWindow().getScaleFactor(); // Get scale factor of the window
            double mouseX = client.mouse.getX() / scaleFactor; // Apply scaling
            double mouseY = client.mouse.getY() / scaleFactor; // Apply scaling

            try {
                HandledScreenAccessor accessor = (HandledScreenAccessor) inventoryScreen;

                Slot hoveredSlot = accessor.invokeGetSlotAt(mouseX, mouseY);
                if (hoveredSlot == null) {
                    return;
                }

                ItemStack hoveredSlotStack = hoveredSlot.getStack();

                // Send packet
                BeltSlotInventoryPacketPayload payload = new BeltSlotInventoryPacketPayload(hoveredSlotStack, hoveredSlot.getIndex());
                ClientPlayNetworking.send(payload);

                lastSwapTime1 = System.currentTimeMillis();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
