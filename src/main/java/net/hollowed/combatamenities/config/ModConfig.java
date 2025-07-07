package net.hollowed.combatamenities.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "combat_amenities")
@Config.Gui.Background("minecraft:textures/block/stripped_spruce_log.png")
public class ModConfig implements ConfigData {
    public int backslotX = 0;
    public int backslotY = 0;

    public boolean backslotParticles = true;
    public int enchantmentParticleChance = 3;
    public int backslotAmbientSoundVolume = 100;
    public int backslotSwapSoundVolume = 100;
    public boolean flipBackslotDisplay = false;

    public boolean flipBeltslotDisplay = false;

    @ConfigEntry.Gui.RequiresRestart
    public boolean removeDurability = true;

    public boolean itemArrows = false;
    public boolean swingThrough = true;
    public boolean correctTridentReturn = true;
    public boolean riptideFix = true;

    @ConfigEntry.Gui.RequiresRestart
    public boolean builtInLoyalty = true;

    public boolean throwableFirecharge = true;
    public boolean shieldTweaks = true;
    public int shieldParryTime = 5;
    public boolean enderPearlTweaks = true;
    public boolean bowTweaks = true;

    @ConfigEntry.Gui.RequiresRestart
    public boolean meleeRework = false;
}
