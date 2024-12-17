package net.hollowed.combatamenities.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "combat_amenities")
@Config.Gui.Background("minecraft:textures/block/stripped_spruce_log.png")
public class ModConfig implements ConfigData {
    @ConfigEntry.Gui.RequiresRestart
    public int backslotX = 0;
    @ConfigEntry.Gui.RequiresRestart
    public int backslotY = 0;

    public boolean backslotParticles = true;
    public int enchantmentParticleChance = 90;
    public boolean backslotSounds = true;

    public boolean removeDurability = true;
    public boolean itemArrows = false;
    public boolean swingThrough = true;
    public boolean correctTridentReturn = true;
    public boolean riptideFix = true;
    public boolean throwableFirecharge = true;
    public boolean shieldTweaks = true;
    public int shieldParryTime = 6;
    public boolean enderPearlTweaks = true;
    public boolean bowTweaks = true;
}
