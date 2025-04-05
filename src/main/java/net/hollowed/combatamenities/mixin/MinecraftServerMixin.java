package net.hollowed.combatamenities.mixin;

import com.mojang.datafixers.DataFixer;
import net.hollowed.combatamenities.ModSounds;
import net.hollowed.combatamenities.util.ItemSlotSoundHandler;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.util.ApiServices;
import net.minecraft.util.Identifier;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Unique
    private static final TagKey<Item> SWORD_TAG = TagKey.of(Registries.ITEM.getKey(), Identifier.ofVanilla("swords"));

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, Proxy proxy, DataFixer dataFixer, ApiServices apiServices, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci) {
        Registries.ITEM.forEach(item -> {
            if (item instanceof ItemSlotSoundHandler soundItem) {
                if (item.getDefaultStack().isIn(SWORD_TAG) || soundItem instanceof AxeItem) {
                    soundItem.combat_Amenities$setUnsheatheSound(ModSounds.SWORD_UNSHEATH);
                }
            }
        });
    }
}
