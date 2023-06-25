package net.Vicente.tutorialmod.night;

import com.google.common.collect.Maps;
import net.Vicente.tutorialmod.server.level.ModServerLevel;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SavedData;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;

public class ModNights extends SavedData {

    private String nightsExist;
    private int nightsLevel;
    private final Map<Integer, ModNight> modNightMap = Maps.newHashMap();
    private int nextAvailableID;
    private final ServerLevel level;
    private final ModServerLevel modServerLevel;
    private int tick;


    public ModNights(ModServerLevel modServerLevel) {
        this.level = modServerLevel.getSl();
        this.modServerLevel = modServerLevel;
        this.nextAvailableID = 1;
        this.setDirty();
    }

    public ModNight get(int num) {
        return this.modNightMap.get(num);
    }

    public void tick() {
        ++this.tick;
        Iterator<ModNight> iterator = this.modNightMap.values().iterator();

        while(iterator.hasNext()) {
            ModNight modNight = iterator.next();
//            if (this.level.getGameRules().getBoolean(GameRules.RULE_DISABLE_RAIDS)) {
//                modNight.stop();
//            }

            if (modNight.isStopped()) {
                iterator.remove();
                this.setDirty();
            } else {
                modNight.tick();
            }
        }

        if (this.tick % 200 == 0) {
            this.setDirty();
        }

//        DebugPackets.sendRaids(this.level, this.modNightMap.values());
    }


    @Nullable
    public ModNight createOrExtendModNight(Player serverPlayer) {
        if (serverPlayer.isSpectator()) {
            return null;
        } else {
            DimensionType dimensiontype = serverPlayer.level.dimensionType();
            if (!dimensiontype.hasRaids()) {
                return null;
            } else {
                BlockPos blockpos = serverPlayer.blockPosition();

                ModNight modNight = this.getOrCreateModNight(this.modServerLevel, blockpos, serverPlayer);

                boolean flag = false;
                if (!modNight.isStarted()) {
                    if (!this.modNightMap.containsKey(modNight.getId())) {
                        this.modNightMap.put(modNight.getId(), modNight);
                    }


                    flag = true;
                } else if (modNight.getBadOmenLevel() < modNight.getMaxBadOmenLevel()) {
                    flag = true;
                }
                this.setDirty();
                return modNight;
            }
        }
    }

    private ModNight getOrCreateModNight(ModServerLevel modServerLevel, BlockPos blockPos, Player serverPlayer) {
        // Check if it exists, if it doesn't exist, create it, if it does then get it
        ModNight modNight = modServerLevel.getModNightAt(blockPos, serverPlayer);
        // could use this.getUniqueId -1 to get the last created Mod Night
        return modNight != null ? modNight : new ModNight(this.getUniqueId(), modServerLevel.getSl(), blockPos);
    }


    public static ModNights load(ModServerLevel level, CompoundTag tag) {
        ModNights modNights = new ModNights(level);
        modNights.nextAvailableID = tag.getInt("NextAvailableID");
        modNights.tick = tag.getInt("Tick");
        ListTag listtag = tag.getList("ModNights", 10);

        for(int i = 0; i < listtag.size(); ++i) {
            CompoundTag compoundtag = listtag.getCompound(i);
            ModNight modNight = new ModNight(modNights.level, compoundtag);
            modNights.modNightMap.put(modNight.getId(), modNight);
        }

        return modNights;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putInt("NextAvailableID", this.nextAvailableID);
        tag.putInt("Tick", this.tick);
        ListTag listtag = new ListTag();

        for(ModNight modNight : this.modNightMap.values()) {
            CompoundTag compoundtag = new CompoundTag();
            modNight.save(compoundtag);
            listtag.add(compoundtag);
        }

        tag.put("Mod_Nights", listtag);
        return tag;
    }

    public static String getFileId(Holder<DimensionType> p_211597_) {
        return p_211597_.is(BuiltinDimensionTypes.END) ? "mod_nights_end" : "mod_nights";
    }


    private int getUniqueId() {
        return ++this.nextAvailableID;
    }

    @Nullable
    public ModNight getNearbyModNight(BlockPos p_37971_, int p_37972_, Player serverPlayer) {
        // could get old location and check if it is near the old location, if it is, then move it to the new location
        ModNight modNight = null;
        double d0 = (double)p_37972_;
        for(ModNight modNight1 : this.modNightMap.values()) {
            double d1 = modNight1.getCenter().distSqr(p_37971_);
            if (modNight1.isActive() && d1 < d0) {
                modNight = modNight1;
                d0 = d1;
            } else if (modNight1.isActive() && d1 > d0){
                Player serverPlayerCreator = modNight1.getCreatorPlayer();
                if(serverPlayerCreator.equals(serverPlayer)){
                    modNight1.setCenter(p_37971_);
                    modNight = modNight1;
                    d0 = d1;
                }
            }
        }

        return modNight;
    }


}
