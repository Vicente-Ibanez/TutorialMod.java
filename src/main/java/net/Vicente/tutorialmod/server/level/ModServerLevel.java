package net.Vicente.tutorialmod.server.level;

import net.Vicente.tutorialmod.night.ModNight;
import net.Vicente.tutorialmod.night.ModNights;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.level.GameRules;

import javax.annotation.Nullable;
import java.util.function.BooleanSupplier;

public class ModServerLevel {
    private final ServerLevel sl;
    protected final ModNights modNights;

    public ModServerLevel(ServerLevel serverLevel){
        sl = serverLevel;
        modNights = sl.getDataStorage().computeIfAbsent((p_184095_) -> {
            return ModNights.load(this, p_184095_);
        }, () -> {
            return new ModNights(this);
        }, ModNights.getFileId(serverLevel.dimensionTypeRegistration()));

    }

    public ServerLevel getSl() {
        return sl;
    }



    @Nullable
    public ModNight getModNightAt(BlockPos p_8833_, Player serverPlayer) {
        return modNights.getNearbyModNight(p_8833_, 9216, serverPlayer);
    }


    public ModNights getModNights() {
        return this.modNights;
    }

    public void tick() {
        this.modNights.tick();
    }
}