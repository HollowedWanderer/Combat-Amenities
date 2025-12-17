package net.hollowed.combatamenities.mixin;

import com.mojang.datafixers.DataFixer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.LevelLoadListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(Thread serverThread, LevelStorageSource.LevelStorageAccess session, PackRepository dataPackManager, WorldStem saveLoader, Proxy proxy, DataFixer dataFixer, Services apiServices, LevelLoadListener chunkLoadProgress, CallbackInfo ci) {

    }
}
