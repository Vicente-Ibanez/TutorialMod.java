package net.Vicente.tutorialmod.night;

import com.google.common.collect.Sets;
import net.Vicente.tutorialmod.entity.ModEntityTypes;
import net.Vicente.tutorialmod.night.nighttype.NightType;
import net.Vicente.tutorialmod.server.level.ModServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class ModNight {

    private final int id;
    private final ServerLevel level;
    private final ModServerLevel modServerLevel;
    private boolean active;
    private int modNightCooldownTicks;
    private static final Component MOD_NIGHT_NAME_COMPONENT = Component.translatable("event.minecraft.modnight");
    private final ServerBossEvent modNightEvent = new ServerBossEvent(MOD_NIGHT_NAME_COMPONENT, BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);
    private BlockPos center;
    private final int numGroups;
    private ModNight.ModNightStatus status;
    private boolean started;
    private long ticksActive;
    private int badOmenLevel;
    private int groupsSpawned;
    private int postModNightTicks;
    private float totalHealth;
    private final RandomSource random = RandomSource.create();
    private static final Component VICTORY = Component.translatable("event.minecraft.mod.night.victory");
    private static final Component DEFEAT = Component.translatable("event.minecraft.mod.night.defeat");
    private static final Component MOD_NIGHT_BAR_VICTORY_COMPONENT = MOD_NIGHT_NAME_COMPONENT.copy().append(" - ").append(VICTORY);
    private static final Component MOD_NIGHT_BAR_DEFEAT_COMPONENT = MOD_NIGHT_NAME_COMPONENT.copy().append(" - ").append(DEFEAT);

    private Optional<BlockPos> waveSpawnPos = Optional.empty();
    private final int modWave;
    private final int type;
    // should save the player than made it, and then if the player moves too far away,
    // just move it to them rather than creating a new mod night

    private final Player creatorPlayer;

    HashMap<EntityType, Integer> modSpawnDict = new HashMap<>();

    public ModNight(int num, ServerLevel serverLevel, BlockPos pos) {
        this.id = num;
        this.level = serverLevel;
        this.modServerLevel = new ModServerLevel(this.level);
        this.active = true;
        this.modNightCooldownTicks = 300;
        this.modNightEvent.setProgress(1.0F);
        this.center = pos;
        this.numGroups = this.getNumGroups(serverLevel.getDifficulty());
        this.status = ModNight.ModNightStatus.ONGOING;
        this.modWave = 0;

        // call function that figures out which type of night battle
        // saves the type of night battle
        // implement type of night battle

        // one problem is that multiple mod nights can occur in a single night,
        // so I want to get it so the type of night is saved universally
        // and accessed or created every time a mod night is made
        this.type = this.nightType(this.level.getDayTime());

        this.creatorPlayer = (!(level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 9216, true) == null)
                ? level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 9216, true):level.getRandomPlayer());

    }

    public ModNight(ServerLevel serverLevel, CompoundTag tag) {
        this.level = serverLevel;
        this.modServerLevel = new ModServerLevel(this.level);
        this.id = tag.getInt("Id");
        this.started = tag.getBoolean("Started");
        this.active = tag.getBoolean("Active");
        this.ticksActive = tag.getLong("TicksActive");
        this.badOmenLevel = tag.getInt("BadOmenLevel");
        this.groupsSpawned = tag.getInt("GroupsSpawned");
        this.modNightCooldownTicks = tag.getInt("PreModNightTicks");
        this.postModNightTicks = tag.getInt("PostModNightTicks");
        this.totalHealth = tag.getFloat("TotalHealth");
        this.center = new BlockPos(tag.getInt("CX"), tag.getInt("CY"), tag.getInt("CZ"));
        this.numGroups = tag.getInt("NumGroups");
        this.status = ModNight.ModNightStatus.getByName(tag.getString("Status"));

        this.modWave = 0;

        this.type = this.nightType(this.level.getDayTime());
        this.creatorPlayer = (!(level.getNearestPlayer(tag.getInt("CX"), tag.getInt("CY"), tag.getInt("CZ"), 9216, true) == null)
                ? level.getNearestPlayer(tag.getInt("CX"), tag.getInt("CY"), tag.getInt("CZ"), 9216, true):level.getRandomPlayer());

    }

    public Player getCreatorPlayer(){
        return creatorPlayer;
    }

    private void nightType(){
        
    }

    public boolean isOver() {
        return this.isVictory() || this.isLoss();
    }


    public boolean hasFirstWaveSpawned() {
        return this.groupsSpawned > 0;
    }

    public boolean isStopped() {
        return this.status == ModNight.ModNightStatus.STOPPED;
    }

    public boolean isVictory() {
        return this.status == ModNight.ModNightStatus.VICTORY;
    }

    public boolean isLoss() {
        return this.status == ModNight.ModNightStatus.LOSS;
    }



    public ServerLevel getLevel() {
        return this.level;
    }

    public boolean isStarted() {
        return this.started;
    }

    public int getGroupsSpawned() {
        return this.groupsSpawned;
    }

    private Predicate<ServerPlayer> validPlayer() {
        return (p_253589_) -> {
            BlockPos blockpos = p_253589_.blockPosition();
            return p_253589_.isAlive() && this.modServerLevel.getModNightAt(blockpos, this.creatorPlayer) == this;
        };
    }

    private void updatePlayers() {
        Set<ServerPlayer> set = Sets.newHashSet(this.modNightEvent.getPlayers());
        List<ServerPlayer> list = this.level.getPlayers(this.validPlayer());

        for(ServerPlayer serverplayer : list) {
            if (!set.contains(serverplayer)) {
                this.modNightEvent.addPlayer(serverplayer);
            }
        }

        for(ServerPlayer serverplayer1 : set) {
            if (!list.contains(serverplayer1)) {
                this.modNightEvent.removePlayer(serverplayer1);
            }
        }

    }

    public int getMaxBadOmenLevel() {
        return 5;
    }

    public int getBadOmenLevel() {
        return this.badOmenLevel;
    }

//    public void setBadOmenLevel(int p_150219_) {
//        this.badOmenLevel = p_150219_;
//    }

    public void absorbBadOmen(Player p_37729_) {
        if (p_37729_.hasEffect(MobEffects.BAD_OMEN)) {
            this.badOmenLevel += p_37729_.getEffect(MobEffects.BAD_OMEN).getAmplifier() + 1;
            this.badOmenLevel = Mth.clamp(this.badOmenLevel, 0, this.getMaxBadOmenLevel());
        }

        p_37729_.removeEffect(MobEffects.BAD_OMEN);
    }

    public void stop() {
        this.active = false;
        this.modNightEvent.removeAllPlayers();
        this.status = ModNight.ModNightStatus.STOPPED;
    }

    public void tick() {
        if (!this.isStopped()) {
            if (this.status == ModNight.ModNightStatus.ONGOING) {
                boolean flag = this.active;
                this.active = this.level.hasChunkAt(this.center);
                if (this.level.getDifficulty() == Difficulty.PEACEFUL) {
                    this.stop();
                    return;
                }

                if (flag != this.active) {
                    this.modNightEvent.setVisible(this.active);
                }

                if (!this.active) {
                    return;
                }


                ++this.ticksActive;
                if (this.ticksActive >= 48000L) {
                    this.stop();
                    return;
                }

                // if the time is exactly on the 1,000 tick mark
                // between 13,000 and 23,000
                if(this.getLevel().dayTime()%1000F == 0){
                    // First divide by 24,000 to take out the 24,000+...
                    modWave((int) (this.getLevel().dayTime() % 24000) / 1000);

                    // if the amount time/24000 is larger than cut off, then spawn harder waves
                    // if amt is lwoer than cutoff, spawn easier waves

                    // need 5 specific times
                    // 13,000 + 24,000 + 24,000...
                    // 15,000 + 24,000 + 24,000...
                    // 17,000 + 24,000 + 24,000...
                    // 19,000 + 24,000 + 24,000...
                    // 21,000 + 24,000 + 24,000...

                }

                int i = 1;

                this.updateBossbar();

                if (this.ticksActive % 20L == 0L) {
                    this.updatePlayers();
                    if (i > 0) {
                        if (i <= 2) {
                            this.modNightEvent.setName(MOD_NIGHT_NAME_COMPONENT.copy().append(" - ").append(Component.translatable("event.minecraft.mod_night.mod_nighters_remaining", i)));
                        } else {
                            this.modNightEvent.setName(MOD_NIGHT_NAME_COMPONENT);
                        }
                    } else {
                        this.modNightEvent.setName(MOD_NIGHT_NAME_COMPONENT);
                    }
                }

                this.setDirty();
            }


        }
    }

    private void modWave(int num) {
        switch(num){
            case(14):
                playSound(this.getCenter());
                // 120 blocks away
                spawnWave(modSpawnDict, num, 5.0F);
                break;
            case(16):
                // 110 blocks away
                modSpawnDict.put(ModEntityTypes.MODZOMBIE.get(), 5);
                modSpawnDict.put(ModEntityTypes.MODWITCH.get(), 2);
                modSpawnDict.put(ModEntityTypes.MODSKELETON.get(), 2);
                spawnWave(modSpawnDict, num, 2.5F);
                break;

            case(18):
                // 70 blocks away
                modSpawnDict.put(ModEntityTypes.MODZOMBIE.get(), 5);
                modSpawnDict.put(ModEntityTypes.MODWITCH.get(), 2);
                modSpawnDict.put(ModEntityTypes.MODSKELETON.get(), 3);
                spawnWave(modSpawnDict, num, 1.5F);
                break;

            case(20):
                // 35 blocks away
                modSpawnDict.put(ModEntityTypes.MODZOMBIE.get(), 5);
                modSpawnDict.put(ModEntityTypes.MODWITCH.get(), 2);
                modSpawnDict.put(ModEntityTypes.MODSKELETON.get(), 3);
                modSpawnDict.put(ModEntityTypes.MODPHANTOM.get(), 3);
                spawnWave(modSpawnDict, num, 0.9F);

                break;
            case(22):
                // 10 blocks away
                // tbd

                break;
        }
    }

    private void spawnWave(HashMap<EntityType, Integer> msd, int num, float distanceNum){
        BlockPos.MutableBlockPos wavePos = new BlockPos.MutableBlockPos();

        // random locations for the groups in a single wave
        for(int group=0; group<6;group++){
            wavePos = generateWavePos(wavePos, num, distanceNum);

            // Lightning, offset vertically to not hit ground/mobs

            EntityType.LIGHTNING_BOLT.spawn(this.getLevel(), (ItemStack) null, null, wavePos,
                    MobSpawnType.TRIGGERED, true, true);
            wavePos.setY(wavePos.getY()-10);

            // Spawn the Mobs
            // each type of mob in a single group of a single wave
            for (Map.Entry<EntityType, Integer> entry : msd.entrySet()) {
                // each mob of a single type in a single group of a single wave
                for(int value = 1; value < entry.getValue(); value++){
                    entry.getKey().spawn(this.getLevel(), (ItemStack) null, null, wavePos,
                            MobSpawnType.TRIGGERED, true, true);
                }

            }


        }

    }

    private BlockPos.MutableBlockPos generateWavePos(BlockPos.MutableBlockPos genWavePos, int num, float distanceNum){
        float f = this.level.random.nextFloat() * ((float)Math.PI * 2F);
        int j = this.center.getX() + Mth.floor(Mth.cos(f) * distanceNum * (float)num) + random.nextInt(5);
        int l = this.center.getZ() + Mth.floor(Mth.sin(f) * distanceNum * (float)num) + random.nextInt(5);
        int k = this.level.getHeight(Heightmap.Types.WORLD_SURFACE, j, l);
        return genWavePos.set(j, k+10, l);
    }

    private void playSound(BlockPos p_37744_) {
        Collection<ServerPlayer> collection = this.modNightEvent.getPlayers();
        long j = this.random.nextLong();

        for(ServerPlayer serverplayer : this.level.players()) {
            Vec3 vec3 = serverplayer.position();
            Vec3 vec31 = Vec3.atCenterOf(p_37744_);
            double d0 = Math.sqrt((vec31.x - vec3.x) * (vec31.x - vec3.x) + (vec31.z - vec3.z) * (vec31.z - vec3.z));
            double d1 = vec3.x + 13.0D / d0 * (vec31.x - vec3.x);
            double d2 = vec3.z + 13.0D / d0 * (vec31.z - vec3.z);
            if (d0 <= 64.0D || collection.contains(serverplayer)) {
                serverplayer.connection.send(new ClientboundSoundPacket(SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(5), SoundSource.NEUTRAL, d1, serverplayer.getY(), d2, 64.0F, 1.0F, j));
            }
        }

    }

    private void spawnGroup(BlockPos p_37756_) {

        DifficultyInstance difficultyinstance = this.level.getCurrentDifficultyAt(p_37756_);
    }

    public void updateBossbar() {
        // Based on the time of Night
        this.modNightEvent.setProgress(1F-(((this.getLevel().dayTime()%24000)-13000F)/10000F));
    }

    private void setDirty() {
        this.modServerLevel.getModNights().setDirty();
    }

    public BlockPos getCenter() {
        return this.center;
    }

    public void setCenter(BlockPos p_37761_) {
        this.center = p_37761_;
    }

    public int getId() {
        return this.id;
    }


    public boolean isActive() {
        return this.active;
    }

    public CompoundTag save(CompoundTag p_37748_) {
        p_37748_.putInt("Id", this.id);
        p_37748_.putBoolean("Started", this.started);
        p_37748_.putBoolean("Active", this.active);
        p_37748_.putLong("TicksActive", this.ticksActive);
        p_37748_.putInt("BadOmenLevel", this.badOmenLevel);
        p_37748_.putInt("GroupsSpawned", this.groupsSpawned);
        p_37748_.putInt("PreModNightTicks", this.modNightCooldownTicks);
        p_37748_.putInt("PostModNightTicks", this.postModNightTicks);
        p_37748_.putFloat("TotalHealth", this.totalHealth);
        p_37748_.putInt("NumGroups", this.numGroups);
        p_37748_.putString("Status", this.status.getName());
        p_37748_.putInt("CX", this.center.getX());
        p_37748_.putInt("CY", this.center.getY());
        p_37748_.putInt("CZ", this.center.getZ());

        return p_37748_;
    }

    public int getNumGroups(Difficulty p_37725_) {
        switch (p_37725_) {
            case EASY:
                return 3;
            case NORMAL:
                return 5;
            case HARD:
                return 7;
            default:
                return 0;
        }
    }

    static enum ModNightStatus {
        ONGOING,
        VICTORY,
        LOSS,
        STOPPED;

        private static final ModNight.ModNightStatus[] VALUES = values();

        static ModNight.ModNightStatus getByName(String p_37804_) {
            for(ModNight.ModNightStatus modNight$modNightstatus : VALUES) {
                if (p_37804_.equalsIgnoreCase(modNight$modNightstatus.name())) {
                    return modNight$modNightstatus;
                }
            }

            return ONGOING;
        }

        public String getName() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    public int nightType(long time){
        // generate random number to decided
        Random random = new Random();
        // Day 24,000 ticks + 13,000 tick offset
        // Calculate the day
        long day = time/24000;
        // also check if player has been to nether/end
        // maybe add an event listener to check this?
        int type = 0;

        // most mc worlds will larger # of days, so inverse if order
        if(day%10==0 && time > 40000) { // Boss fight every 10 days
            // Boss fight
            type = -1*random.nextInt(10);
            // boss fights are a negative int
        }else if(day < 10){
            // zombie hoards
            // 4 types
            type = random.nextInt(4);
        }else if(day < 20){
            // above and skel, and spider
            // 2 skel types, 2 spider types
            type = random.nextInt(8);
        }else if(day < 40){
            // above and witches, phatoms, etc
            // 1 witch type 1 phantom type
            type = random.nextInt(10);
        }else if(day < 100){
            // above and more content
            // tba
        }

        // use the number of days to adjust the strength of the mobs
        switch(type){
            case 1:
                // zombie 1
                modSpawnDict.put(ModEntityTypes.MODZOMBIE.get(), 5);

                break;
            case 2:
                // zombie 2
                modSpawnDict.put(ModEntityTypes.MODZOMBIE.get(), 6);

                break;
            case 3:
                // zombie 3
                modSpawnDict.put(ModEntityTypes.MODZOMBIE.get(), 7);
                break;
            case 4:
                // zombie 4
                modSpawnDict.put(ModEntityTypes.MODZOMBIE.get(), 8);
                break;
            case 5:
                // Skel 1
                break;
            case 6:
                // Skel 2
                break;
            case 7:
                // Spider 1
                break;
            case 8:
                // Spider 2
                break;
        }


        return 1;
    }

}
