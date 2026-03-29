package net.hollowed.combatamenities.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class CAConfig extends MidnightConfig {
    private static final String BACK = "back";
    private static final String BELT = "belt";
    private static final String SLOTS = "slots";
    private static final String TWEAKS = "tweaks";

    @SuppressWarnings("unused") @Comment(category = BACK, centered = true, name = "Back Slot Options")
    public static Comment backConfig;
    @SuppressWarnings("unused") @Entry(category = BACK, name = "Enable Back Slot - Doesn't Work") @Server
    public static boolean enableBackslot = true;
    @Entry(category = BACK, name = "Back Slot HUD X", isSlider = true, min = -312, max = 95) @Client
    public static int backslotX = 0;
    @Entry(category = BACK, name = "Back Slot HUD Y", isSlider = true, min = 0, max = 227) @Client
    public static int backslotY = 0;
    @Entry(category = BACK, name = "Back Slot Enchantment Particles Enabled") @Client
    public static boolean backslotParticles = true;
    @Entry(category = BACK, name = "Flip Back Slot Display") @Client
    public static boolean flipBackslotDisplay = false;

    @SuppressWarnings("unused") @Comment(category = BELT, centered = true, name = "Belt Slot Options")
    public static Comment beltConfig;
    @SuppressWarnings("unused") @Entry(category = BELT, name = "Enable Belt Slot - Doesn't Work") @Server
    public static boolean enableBeltslot = true;
    @Entry(category = BELT, name = "Belt Slot HUD X", isSlider = true, min = -335, max = 72) @Client
    public static int beltslotX = 0;
    @Entry(category = BELT, name = "Belt Slot HUD Y", isSlider = true, min = 0, max = 227) @Client
    public static int beltslotY = 0;
    @Entry(category = BELT, name = "Belt Slot Enchantment Particles Enabled") @Client
    public static boolean beltslotParticles = true;
    @Entry(category = BELT, name = "Flip Belt Slot Display") @Client
    public static boolean flipBeltslotDisplay = false;

    @SuppressWarnings("unused") @Comment(category = SLOTS, centered = true, name = "General Slots Options")
    public static Comment slotsConfig;
    @Entry(category = SLOTS, name = "Enchantment Particle Spawn Chance") @Client
    public static float enchantmentParticleChance = 0.5F;
    @Entry(category = SLOTS, name = "Slot Ambient Sound Volume") @Client
    public static int backslotAmbientSoundVolume = 100;
    @Entry(category = SLOTS, name = "Slot Swap Sound Volume") @Client
    public static int backslotSwapSoundVolume = 100;

    @SuppressWarnings("unused") @Comment(category = TWEAKS, centered = true, name = "General Tweaks Options")
    public static Comment tweaksConfig;
    @Entry(category = TWEAKS, name = "Render Arrows As Items") @Client
    public static boolean itemArrows = false;
    @Entry(category = TWEAKS, name = "Fix Trident Loyalty Return") @Server
    public static boolean correctTridentReturn = true;
    @Entry(category = TWEAKS, name = "Fix Riptide First Person Rendering") @Client
    public static boolean riptideFix = true;
    @Entry(category = TWEAKS, name = "Built In Loyalty") @Server
    public static boolean builtInLoyalty = true;
    @Entry(category = TWEAKS, name = "Throwable Fire Charges") @Server
    public static boolean throwableFirecharge = true;
    @Entry(category = TWEAKS, name = "Shield Tweaks") @Server
    public static boolean shieldTweaks = true;
    @Entry(category = TWEAKS, name = "Shield Parry Window (Ticks)") @Server
    public static int shieldParryTime = 5;
    @Entry(category = TWEAKS, name = "Ender Pearl Tweaks") @Server
    public static boolean enderPearlTweaks = true;
    @Entry(category = TWEAKS, name = "Bow Tweaks") @Server
    public static boolean bowTweaks = true;
}
