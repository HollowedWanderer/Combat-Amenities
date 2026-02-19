![Title of the mod](https://cdn.modrinth.com/data/cached_images/f9d1ab74bd75035b0b3367cd28c41b68f89d201d.png)

The majority of features are not available, and won't be, on 1.20.1, nor will issues for that version be fixed.

---
### Important Information

- **Updates/Backports** - I plan to keep the mod on the latest version of the game - There will be No backports. If it is not updated 3 microseconds after a version releases please do not ask me to update this mod. 

---
### Bug Reports/Suggestions
Don't hesitate to report issues or suggestions [here](https://github.com/HollowedWanderer/Combat-Amenities/issues)! It really helps me out a lot!

Also make sure to read the important information section before posting an issue please, it might answer some questions you have.

# Features
Combat Amenities aims to improve combat with some small tweaks, such as adding a back and belt slot!

![Back slot enchantment particles display](https://cdn.modrinth.com/data/cached_images/47bad7cab4d9a6996f9f70074eca8c2b04e68448.png)

### Feature List:

- **Back Slot** - A back slot to display weapons to your friends while also having them on standby with a quick keybind (R by default)! The item in your back slot is visible in third person on your back, and in first person via an offhand-like slot on the right side of your hotbar. The item on your back will have a fancy animation similar to the cape, the amount an item sways is configurable per item. The position of the overlay in the HUD is also configurable. It will also now play sounds when walking and landing on the ground when wearing an item in your back slot (configurable). 

- **Belt Slot** - Another inventory slot similar to the back slot, however, this one is displayed on the players waist. The belt slot also works with a quick keybind (V by default). (configurable). 

- **Back Slot Json-able Configuration** - You can now create json files in texture packs to change the size, position, rotation, model transformation mode, and sway amount for any item from any mod! There is a file, `default.json`, on the GitHub page [here for the back slot](https://github.com/HollowedWanderer/Combat-Amenities/blob/master/src/main/resources/assets/combatamenities/backslot_transforms/default.json) and [here for the belt slot](https://github.com/HollowedWanderer/Combat-Amenities/blob/master/src/main/resources/assets/combatamenities/beltslot_transforms/default.json)  that can be used as an example to add any item configuration to a texture pack!

- **Trident Tweaks** - Tridents now return to the correct slot, the slot they were thrown from! When using riptide, offhand items are hidden in first person to prevent them from blocking visibility. Both of these things are configurable. Tridents now also use their item model for the projectile which allows the making of texture packs to change the trident.

- **Bow/Arrow Tweaks** - Bows now start shaking and will become inaccurate if drawn back for too long (Configurable). Arrows now can optionally have the projectile be rendered as the arrow's item sprite!

- **Shield Tweaks** - Shields now only block 75% of damage when blocking normally, however, you cannot die from taking damage through a shield, it will leave you at half a heart (Configurable). For 6 ticks (Configurable) after you start using a shield it will be able to parry attacks. When an attack is parried, it launches the attacker backwards, if it is a projectile, it will be reflected back towards where it came from.

- **Ender Pearl Tweaks** - Ender pearls have to be charged like a bow now (Configurable). The charge amount determines the velocity. They also don't deal fall damage either (Configurable).

- **Fire Charge Changes** - They can now be thrown (Configurable).

- Some other small things.

### Forge Port/Back ports
I will not be porting this mod to Forge or NeoForge, and it may or may not be compatible with Sinytra Connector on the latest versions, so it might not be usable in NeoForge modpacks.

---

### Make sure to check out the gallery!

Also ty Famine for the unsheath sound and to Mercury for help with some of the code!
