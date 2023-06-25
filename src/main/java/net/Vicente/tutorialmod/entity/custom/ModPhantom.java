package net.Vicente.tutorialmod.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class ModPhantom extends Phantom {
    public static final float FLAP_DEGREES_PER_TICK = 7.448451F;
    public static final int TICKS_PER_FLAP = Mth.ceil(24.166098F);
    private static final EntityDataAccessor<Integer> ID_SIZE = SynchedEntityData.defineId(ModPhantom.class, EntityDataSerializers.INT);
    Vec3 moveTargetPoint = Vec3.ZERO;
    BlockPos anchorPoint = BlockPos.ZERO;
    ModPhantom.AttackPhase attackPhase = ModPhantom.AttackPhase.CIRCLE;

    public ModPhantom(EntityType<? extends ModPhantom> p_33101_, Level p_33102_) {
        super(p_33101_, p_33102_);
        this.xpReward = 10;
        this.moveControl = new ModPhantom.ModPhantomMoveControl(this);
        this.lookControl = new ModPhantom.ModPhantomLookControl(this);
    }

    public boolean isFlapping() {
        return (this.getUniqueFlapTickOffset() + this.tickCount) % TICKS_PER_FLAP == 0;
    }

    protected BodyRotationControl createBodyControl() {
        return new ModPhantom.ModPhantomBodyRotationControl(this);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(3, new ModPhantom.ModPhantomAttackStrategyGoal());
        this.goalSelector.addGoal(2, new ModPhantom.ModPhantomSweepAttackGoal());
        this.goalSelector.addGoal(4, new ModPhantom.ModPhantomCircleAroundAnchorGoal());
        this.goalSelector.addGoal(2, new ModPhantom.ModPhantomMine(this));
        this.targetSelector.addGoal(2, new ModPhantom.ModPhantomAttackPlayerTargetGoal());
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));

    }

    static class ModPhantomMine extends Goal {
        private final ModPhantom modPhantom;

        public ModPhantomMine(ModPhantom p_32585_) {
            this.modPhantom = p_32585_;
        }

        public boolean canUse() {
            if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.modPhantom.level, this.modPhantom)) {
                return false;
            } else {
                return true;
            }
        }

        public void tick() {
            RandomSource randomsource = this.modPhantom.getRandom();
            Level level = this.modPhantom.level;
            int i = Mth.floor(this.modPhantom.getX() - 2.0D + randomsource.nextDouble() * 5.0D);
            int j = Mth.floor(this.modPhantom.getY() + randomsource.nextDouble() * 4.0D);
            int k = Mth.floor(this.modPhantom.getZ() - 2.0D + randomsource.nextDouble() * 5.0D);

            // Destory 2 blocks at once
            BlockPos blockpos_1 = new BlockPos(i, j, k);
            BlockPos blockpos_2 = new BlockPos(i, j+1.0D, k);
            BlockPos blockpos_3 = new BlockPos(i+1.0D, j, k);
            BlockPos blockpos_4 = new BlockPos(i, j, k+1.0D);
            BlockState blockstate = level.getBlockState(blockpos_1);

            Vec3 vec3 = new Vec3((double)this.modPhantom.getBlockX() + 0.5D, (double)j + 0.5D, (double)this.modPhantom.getBlockZ() + 0.5D);
            Vec3 vec31 = new Vec3((double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D);
            BlockHitResult blockhitresult = level.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this.modPhantom));
            boolean flag = blockhitresult.getBlockPos().equals(blockpos_1);

            if(flag){
                level.removeBlock(blockpos_1, false);
                level.gameEvent(GameEvent.BLOCK_DESTROY, blockpos_1, GameEvent.Context.of(this.modPhantom, blockstate));

                level.removeBlock(blockpos_2, false);
                level.gameEvent(GameEvent.BLOCK_DESTROY, blockpos_2, GameEvent.Context.of(this.modPhantom, blockstate));

                level.removeBlock(blockpos_2, false);
                level.gameEvent(GameEvent.BLOCK_DESTROY, blockpos_3, GameEvent.Context.of(this.modPhantom, blockstate));

                level.removeBlock(blockpos_2, false);
                level.gameEvent(GameEvent.BLOCK_DESTROY, blockpos_4, GameEvent.Context.of(this.modPhantom, blockstate));
            }

        }
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(ID_SIZE, 0);
    }

    public void setModPhantomSize(int p_33109_) {
        this.entityData.set(ID_SIZE, Mth.clamp(p_33109_, 0, 64));
    }

    private void updateModPhantomSizeInfo() {
        this.refreshDimensions();
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((double)(6 + this.getModPhantomSize()));
    }

    public int getModPhantomSize() {
        return this.entityData.get(ID_SIZE);
    }

    protected float getStandingEyeHeight(Pose p_33136_, EntityDimensions p_33137_) {
        return p_33137_.height * 0.35F;
    }

    public void onSyncedDataUpdated(EntityDataAccessor<?> p_33134_) {
        if (ID_SIZE.equals(p_33134_)) {
            this.updateModPhantomSizeInfo();
        }

        super.onSyncedDataUpdated(p_33134_);
    }

    public int getUniqueFlapTickOffset() {
        return this.getId() * 3;
    }

    protected boolean shouldDespawnInPeaceful() {
        return true;
    }

    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            float f = Mth.cos((float)(this.getUniqueFlapTickOffset() + this.tickCount) * 7.448451F * ((float)Math.PI / 180F) + (float)Math.PI);
            float f1 = Mth.cos((float)(this.getUniqueFlapTickOffset() + this.tickCount + 1) * 7.448451F * ((float)Math.PI / 180F) + (float)Math.PI);
            if (f > 0.0F && f1 <= 0.0F) {
                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.PHANTOM_FLAP, this.getSoundSource(), 0.95F + this.random.nextFloat() * 0.05F, 0.95F + this.random.nextFloat() * 0.05F, false);
            }

            int i = this.getModPhantomSize();
            float f2 = Mth.cos(this.getYRot() * ((float)Math.PI / 180F)) * (1.3F + 0.21F * (float)i);
            float f3 = Mth.sin(this.getYRot() * ((float)Math.PI / 180F)) * (1.3F + 0.21F * (float)i);
            float f4 = (0.3F + f * 0.45F) * ((float)i * 0.2F + 1.0F);
            this.level.addParticle(ParticleTypes.MYCELIUM, this.getX() + (double)f2, this.getY() + (double)f4, this.getZ() + (double)f3, 0.0D, 0.0D, 0.0D);
            this.level.addParticle(ParticleTypes.MYCELIUM, this.getX() - (double)f2, this.getY() + (double)f4, this.getZ() - (double)f3, 0.0D, 0.0D, 0.0D);
        }

    }

    public void aiStep() {
        if (this.isAlive() && this.isSunBurnTick()) {
            this.setSecondsOnFire(8);
        }

        super.aiStep();
    }

    protected void customServerAiStep() {
        super.customServerAiStep();
    }

    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_33126_, DifficultyInstance p_33127_, MobSpawnType p_33128_, @Nullable SpawnGroupData p_33129_, @Nullable CompoundTag p_33130_) {
        this.anchorPoint = this.blockPosition().above(5);
        this.setModPhantomSize(0);
        return super.finalizeSpawn(p_33126_, p_33127_, p_33128_, p_33129_, p_33130_);
    }

    public void readAdditionalSaveData(CompoundTag p_33132_) {
        super.readAdditionalSaveData(p_33132_);
        if (p_33132_.contains("AX")) {
            this.anchorPoint = new BlockPos(p_33132_.getInt("AX"), p_33132_.getInt("AY"), p_33132_.getInt("AZ"));
        }

        this.setModPhantomSize(p_33132_.getInt("Size"));
    }

    public void addAdditionalSaveData(CompoundTag p_33141_) {
        super.addAdditionalSaveData(p_33141_);
        p_33141_.putInt("AX", this.anchorPoint.getX());
        p_33141_.putInt("AY", this.anchorPoint.getY());
        p_33141_.putInt("AZ", this.anchorPoint.getZ());
        p_33141_.putInt("Size", this.getModPhantomSize());
    }

    public boolean shouldRenderAtSqrDistance(double p_33107_) {
        return true;
    }

    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.PHANTOM_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource p_33152_) {
        return SoundEvents.PHANTOM_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.PHANTOM_DEATH;
    }

    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    protected float getSoundVolume() {
        return 1.0F;
    }

    public boolean canAttackType(EntityType<?> p_33111_) {
        return true;
    }

    public EntityDimensions getDimensions(Pose p_33113_) {
        int i = this.getModPhantomSize();
        EntityDimensions entitydimensions = super.getDimensions(p_33113_);
        float f = (entitydimensions.width + 0.2F * (float)i) / entitydimensions.width;
        return entitydimensions.scale(f);
    }

    static enum AttackPhase {
        CIRCLE,
        SWOOP;
    }

    class ModPhantomAttackPlayerTargetGoal extends Goal {
        private final TargetingConditions attackTargeting = TargetingConditions.forCombat().range(64.0D);
        private int nextScanTick = reducedTickDelay(20);

        public boolean canUse() {
            if (this.nextScanTick > 0) {
                --this.nextScanTick;
                return false;
            } else {
                this.nextScanTick = reducedTickDelay(60);
                List<Player> list = ModPhantom.this.level.getNearbyPlayers(this.attackTargeting, ModPhantom.this, ModPhantom.this.getBoundingBox().inflate(16.0D, 64.0D, 16.0D));
                if (!list.isEmpty()) {
                    list.sort(Comparator.<Entity, Double>comparing(Entity::getY).reversed());

                    for(Player player : list) {
                        if (ModPhantom.this.canAttack(player, TargetingConditions.DEFAULT)) {
                            ModPhantom.this.setTarget(player);
                            return true;
                        }
                    }
                }

                return false;
            }
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = ModPhantom.this.getTarget();
            return livingentity != null ? ModPhantom.this.canAttack(livingentity, TargetingConditions.DEFAULT) : false;
        }
    }


    class HurtByTargetGoal extends TargetGoal {
        private static final TargetingConditions HURT_BY_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting();
        private static final int ALERT_RANGE_Y = 10;
        private boolean alertSameType;
        private int timestamp;
        private final Class<?>[] toIgnoreDamage;
        @Nullable
        private Class<?>[] toIgnoreAlert;

        public HurtByTargetGoal(FlyingMob p_26039_, Class<?>... p_26040_) {
            super(p_26039_, true);
            this.toIgnoreDamage = p_26040_;
            this.setFlags(EnumSet.of(Goal.Flag.TARGET));
        }

        public boolean canUse() {
            int i = this.mob.getLastHurtByMobTimestamp();
            LivingEntity livingentity = this.mob.getLastHurtByMob();
            if (i != this.timestamp && livingentity != null) {
                if (livingentity.getType() == EntityType.PLAYER && this.mob.level.getGameRules().getBoolean(GameRules.RULE_UNIVERSAL_ANGER)) {
                    return false;
                } else {
                    for(Class<?> oclass : this.toIgnoreDamage) {
                        if (oclass.isAssignableFrom(livingentity.getClass())) {
                            return false;
                        }
                    }

                    return this.canAttack(livingentity, HURT_BY_TARGETING);
                }
            } else {
                return false;
            }
        }

        public HurtByTargetGoal setAlertOthers(Class<?>... p_26045_) {
            this.alertSameType = true;
            this.toIgnoreAlert = p_26045_;
            return this;
        }

        public void start() {
            this.mob.setTarget(this.mob.getLastHurtByMob());
            this.targetMob = this.mob.getTarget();
            this.timestamp = this.mob.getLastHurtByMobTimestamp();
            this.unseenMemoryTicks = 300;
            if (this.alertSameType) {
                this.alertOthers();
            }

            super.start();
        }

        protected void alertOthers() {
            double d0 = this.getFollowDistance();
            AABB aabb = AABB.unitCubeFromLowerCorner(this.mob.position()).inflate(d0, 10.0D, d0);
            List<? extends Mob> list = this.mob.level.getEntitiesOfClass(this.mob.getClass(), aabb, EntitySelector.NO_SPECTATORS);
            Iterator iterator = list.iterator();

            while(true) {
                Mob mob;
                while(true) {
                    if (!iterator.hasNext()) {
                        return;
                    }

                    mob = (Mob)iterator.next();
                    if (this.mob != mob && mob.getTarget() == null && (!(this.mob instanceof TamableAnimal) || ((TamableAnimal)this.mob).getOwner() == ((TamableAnimal)mob).getOwner()) && !mob.isAlliedTo(this.mob.getLastHurtByMob())) {
                        if (this.toIgnoreAlert == null) {
                            break;
                        }

                        boolean flag = false;

                        for(Class<?> oclass : this.toIgnoreAlert) {
                            if (mob.getClass() == oclass) {
                                flag = true;
                                break;
                            }
                        }

                        if (!flag) {
                            break;
                        }
                    }
                }

                this.alertOther(mob, this.mob.getLastHurtByMob());
            }
        }

        protected void alertOther(Mob p_26042_, LivingEntity p_26043_) {
            p_26042_.setTarget(p_26043_);
        }
    }


    class ModPhantomAttackStrategyGoal extends Goal {
        private int nextSweepTick;

        public boolean canUse() {
            LivingEntity livingentity = ModPhantom.this.getTarget();
            return livingentity != null ? ModPhantom.this.canAttack(livingentity, TargetingConditions.DEFAULT) : false;
        }

        public void start() {
            this.nextSweepTick = this.adjustedTickDelay(10);
            ModPhantom.this.attackPhase = ModPhantom.AttackPhase.CIRCLE;
            this.setAnchorAboveTarget();
        }

        public void stop() {
            ModPhantom.this.anchorPoint = ModPhantom.this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, ModPhantom.this.anchorPoint).above(10 + ModPhantom.this.random.nextInt(20));
        }

        public void tick() {
            if (ModPhantom.this.attackPhase == ModPhantom.AttackPhase.CIRCLE) {
//                --this.nextSweepTick;
                this.nextSweepTick = this.nextSweepTick - 2;
                if (this.nextSweepTick <= 0) {
                    ModPhantom.this.attackPhase = ModPhantom.AttackPhase.SWOOP;
                    this.setAnchorAboveTarget();
                    this.nextSweepTick = this.adjustedTickDelay((8 + ModPhantom.this.random.nextInt(4)) * 20);
                    ModPhantom.this.playSound(SoundEvents.PHANTOM_SWOOP, 10.0F, 0.95F + ModPhantom.this.random.nextFloat() * 0.1F);
                }
            }

        }

        private void setAnchorAboveTarget() {
            ModPhantom.this.anchorPoint = ModPhantom.this.getTarget().blockPosition().above(20 + ModPhantom.this.random.nextInt(20));
            if (ModPhantom.this.anchorPoint.getY() < ModPhantom.this.level.getSeaLevel()) {
                ModPhantom.this.anchorPoint = new BlockPos(ModPhantom.this.anchorPoint.getX(), ModPhantom.this.level.getSeaLevel() + 1, ModPhantom.this.anchorPoint.getZ());
            }

        }
    }

    class ModPhantomBodyRotationControl extends BodyRotationControl {
        public ModPhantomBodyRotationControl(Mob p_33216_) {
            super(p_33216_);
        }

        public void clientTick() {
            ModPhantom.this.yHeadRot = ModPhantom.this.yBodyRot;
            ModPhantom.this.yBodyRot = ModPhantom.this.getYRot();
        }
    }

    class ModPhantomCircleAroundAnchorGoal extends ModPhantom.ModPhantomMoveTargetGoal {
        private float angle;
        private float distance;
        private float height;
        private float clockwise;

        public boolean canUse() {
            return ModPhantom.this.getTarget() == null || ModPhantom.this.attackPhase == ModPhantom.AttackPhase.CIRCLE;
        }

        public void start() {
            this.distance = 5.0F + ModPhantom.this.random.nextFloat() * 10.0F;
            this.height = -4.0F + ModPhantom.this.random.nextFloat() * 9.0F;
            this.clockwise = ModPhantom.this.random.nextBoolean() ? 1.0F : -1.0F;
            this.selectNext();
        }

        public void tick() {
            if (ModPhantom.this.random.nextInt(this.adjustedTickDelay(350)) == 0) {
                this.height = -4.0F + ModPhantom.this.random.nextFloat() * 9.0F;
            }

            if (ModPhantom.this.random.nextInt(this.adjustedTickDelay(250)) == 0) {
                ++this.distance;
                if (this.distance > 15.0F) {
                    this.distance = 5.0F;
                    this.clockwise = -this.clockwise;
                }
            }

            if (ModPhantom.this.random.nextInt(this.adjustedTickDelay(450)) == 0) {
                this.angle = ModPhantom.this.random.nextFloat() * 2.0F * (float)Math.PI;
                this.selectNext();
            }

            if (this.touchingTarget()) {
                this.selectNext();
            }

            if (ModPhantom.this.moveTargetPoint.y < ModPhantom.this.getY() && !ModPhantom.this.level.isEmptyBlock(ModPhantom.this.blockPosition().below(1))) {
                this.height = Math.max(1.0F, this.height);
                this.selectNext();
            }

            if (ModPhantom.this.moveTargetPoint.y > ModPhantom.this.getY() && !ModPhantom.this.level.isEmptyBlock(ModPhantom.this.blockPosition().above(1))) {
                this.height = Math.min(-1.0F, this.height);
                this.selectNext();
            }

        }

        private void selectNext() {
            if (BlockPos.ZERO.equals(ModPhantom.this.anchorPoint)) {
                ModPhantom.this.anchorPoint = ModPhantom.this.blockPosition();
            }

            this.angle += this.clockwise * 15.0F * ((float)Math.PI / 180F);
            ModPhantom.this.moveTargetPoint = Vec3.atLowerCornerOf(ModPhantom.this.anchorPoint).add((double)(this.distance * Mth.cos(this.angle)), (double)(-4.0F + this.height), (double)(this.distance * Mth.sin(this.angle)));
        }
    }

    class ModPhantomLookControl extends LookControl {
        public ModPhantomLookControl(Mob p_33235_) {
            super(p_33235_);
        }

        public void tick() {
        }
    }

    class ModPhantomMoveControl extends MoveControl {
        private float speed = 0.1F;

        public ModPhantomMoveControl(Mob p_33241_) {
            super(p_33241_);
        }

        public void tick() {
            if (ModPhantom.this.horizontalCollision) {
                ModPhantom.this.setYRot(ModPhantom.this.getYRot() + 180.0F);
                this.speed = 0.1F;
            }

            double d0 = ModPhantom.this.moveTargetPoint.x - ModPhantom.this.getX();
            double d1 = ModPhantom.this.moveTargetPoint.y - ModPhantom.this.getY();
            double d2 = ModPhantom.this.moveTargetPoint.z - ModPhantom.this.getZ();
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            if (Math.abs(d3) > (double)1.0E-5F) {
                double d4 = 1.0D - Math.abs(d1 * (double)0.7F) / d3;
                d0 *= d4;
                d2 *= d4;
                d3 = Math.sqrt(d0 * d0 + d2 * d2);
                double d5 = Math.sqrt(d0 * d0 + d2 * d2 + d1 * d1);
                float f = ModPhantom.this.getYRot();
                float f1 = (float)Mth.atan2(d2, d0);
                float f2 = Mth.wrapDegrees(ModPhantom.this.getYRot() + 90.0F);
                float f3 = Mth.wrapDegrees(f1 * (180F / (float)Math.PI));
                ModPhantom.this.setYRot(Mth.approachDegrees(f2, f3, 4.0F) - 90.0F);
                ModPhantom.this.yBodyRot = ModPhantom.this.getYRot();
                if (Mth.degreesDifferenceAbs(f, ModPhantom.this.getYRot()) < 3.0F) {
                    this.speed = Mth.approach(this.speed, 1.8F, 0.005F * (1.8F / this.speed));
                } else {
                    this.speed = Mth.approach(this.speed, 0.2F, 0.025F);
                }

                float f4 = (float)(-(Mth.atan2(-d1, d3) * (double)(180F / (float)Math.PI)));
                ModPhantom.this.setXRot(f4);
                float f5 = ModPhantom.this.getYRot() + 90.0F;
                double d6 = (double)(this.speed * Mth.cos(f5 * ((float)Math.PI / 180F))) * Math.abs(d0 / d5);
                double d7 = (double)(this.speed * Mth.sin(f5 * ((float)Math.PI / 180F))) * Math.abs(d2 / d5);
                double d8 = (double)(this.speed * Mth.sin(f4 * ((float)Math.PI / 180F))) * Math.abs(d1 / d5);
                Vec3 vec3 = ModPhantom.this.getDeltaMovement();
                ModPhantom.this.setDeltaMovement(vec3.add((new Vec3(d6, d8, d7)).subtract(vec3).scale(0.2D)));
            }

        }
    }

    abstract class ModPhantomMoveTargetGoal extends Goal {
        public ModPhantomMoveTargetGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        protected boolean touchingTarget() {
            return ModPhantom.this.moveTargetPoint.distanceToSqr(ModPhantom.this.getX(), ModPhantom.this.getY(), ModPhantom.this.getZ()) < 4.0D;
        }
    }

    class ModPhantomSweepAttackGoal extends ModPhantom.ModPhantomMoveTargetGoal {
        private static final int CAT_SEARCH_TICK_DELAY = 20;
        private boolean isScaredOfCat;
        private int catSearchTick;

        public boolean canUse() {
            return ModPhantom.this.getTarget() != null && ModPhantom.this.attackPhase == ModPhantom.AttackPhase.SWOOP;
        }

        public boolean canContinueToUse() {
            LivingEntity livingentity = ModPhantom.this.getTarget();
            if (livingentity == null) {
                return false;
            } else if (!livingentity.isAlive()) {
                return false;
            } else {
                if (livingentity instanceof Player) {
                    Player player = (Player)livingentity;
                    if (livingentity.isSpectator() || player.isCreative()) {
                        return false;
                    }
                }

                if (!this.canUse()) {
                    return false;
                } else {
                    if (ModPhantom.this.tickCount > this.catSearchTick) {
                        this.catSearchTick = ModPhantom.this.tickCount + 20;
                        List<Cat> list = ModPhantom.this.level.getEntitiesOfClass(Cat.class, ModPhantom.this.getBoundingBox().inflate(16.0D), EntitySelector.ENTITY_STILL_ALIVE);

                        for(Cat cat : list) {
                            cat.hiss();
                        }

                        this.isScaredOfCat = !list.isEmpty();
                    }

                    return !this.isScaredOfCat;
                }
            }
        }

        public void start() {
        }

        public void stop() {
            ModPhantom.this.setTarget((LivingEntity)null);
            ModPhantom.this.attackPhase = ModPhantom.AttackPhase.CIRCLE;
        }

        public void tick() {
            LivingEntity livingentity = ModPhantom.this.getTarget();
            if (livingentity != null) {
                ModPhantom.this.moveTargetPoint = new Vec3(livingentity.getX(), livingentity.getY(0.5D), livingentity.getZ());
                if (ModPhantom.this.getBoundingBox().inflate((double)0.2F).intersects(livingentity.getBoundingBox())) {
                    ModPhantom.this.doHurtTarget(livingentity);
                    ModPhantom.this.attackPhase = ModPhantom.AttackPhase.CIRCLE;
                    if (!ModPhantom.this.isSilent()) {
                        ModPhantom.this.level.levelEvent(1039, ModPhantom.this.blockPosition(), 0);
                    }
                } else if (ModPhantom.this.horizontalCollision || ModPhantom.this.hurtTime > 25) {
                    ModPhantom.this.attackPhase = ModPhantom.AttackPhase.CIRCLE;
                }

            }
        }
    }

    public static AttributeSupplier setAttributes(){
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0F)
                .add(Attributes.FOLLOW_RANGE, 200)
                .add(Attributes.MOVEMENT_SPEED, (double)0.3F).build();
    }

    public boolean doHurtTarget(Entity entity) {
        boolean flag = super.doHurtTarget(entity);
        if (entity instanceof Player) {
            entity.setYRot(this.getYRot());
            entity.setXRot(this.getXRot());
            entity.startRiding(this);
        }

        return flag;
    }

}
