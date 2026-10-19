package com.example.entity;

import com.example.ModEntities;
import com.example.ModSounds;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class Banshee extends Monster implements GeoEntity {
	public static final int WATCHING = 0, STALKING = 1, HUNTING = 2;
	static final int NONE = 0, SPOTTED = 1, JUMPSCARE = 2, MELEE = 3, SKULL = 4, WAIL = 5;

	static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(Banshee.class, EntityDataSerializers.INT);
	static final EntityDataAccessor<Integer> PHANTASM = SynchedEntityData.defineId(Banshee.class, EntityDataSerializers.INT);
	static final EntityDataAccessor<Boolean> DARK = SynchedEntityData.defineId(Banshee.class, EntityDataSerializers.BOOLEAN);

	static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.banshee.idle");
	static final RawAnimation STALK = RawAnimation.begin().thenLoop("animation.banshee.stalk");
	static final RawAnimation DEATH = RawAnimation.begin().thenPlayAndHold("animation.banshee.death");

	final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	final Map<UUID, Integer> gaze = new HashMap<>();

	int action, actionTick, phaseTimer = 300, meleeCd, skullCd = 40, wailCd = 200;
	boolean faded, creeping, fury;
	Player prey;

	public float fade, fadeO;

	public Banshee(EntityType<? extends Banshee> type, Level level) {
		super(type, level);
		moveControl = new Drift();
		setNoGravity(true);
		xpReward = 15;
	}

	public static AttributeSupplier.Builder attrs() {
		return Monster.createMonsterAttributes()
				.add(Attributes.MAX_HEALTH, 60)
				.add(Attributes.ATTACK_DAMAGE, 7)
				.add(Attributes.MOVEMENT_SPEED, 0.25)
				.add(Attributes.FLYING_SPEED, 0.3)
				.add(Attributes.FOLLOW_RANGE, 64)
				.add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
	}

	public static boolean canSpawn(EntityType<Banshee> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource random) {
		return Monster.checkMonsterSpawnRules(type, level, reason, pos, random) && (reason != MobSpawnType.NATURAL || level.getLevel().isNight());
	}

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder b) {
		super.defineSynchedData(b);
		b.define(PHASE, WATCHING);
		b.define(PHANTASM, 0);
		b.define(DARK, false);
	}

	@Override
	protected void registerGoals() {
		goalSelector.addGoal(1, new Act());
		goalSelector.addGoal(2, new Hunt());
		goalSelector.addGoal(3, new Stalk());
		goalSelector.addGoal(4, new Watch());
		goalSelector.addGoal(6, new Roam());
		goalSelector.addGoal(7, new RandomLookAroundGoal(this));
		targetSelector.addGoal(1, new HurtByTargetGoal(this));
	}

	public int phase() {
		return entityData.get(PHASE);
	}

	public int phantasm() {
		return entityData.get(PHANTASM);
	}

	@Override
	public void tick() {
		noPhysics = true;
		super.tick();
		noPhysics = false;
		setNoGravity(true);
		if (!level().isClientSide)
			return;
		fadeO = fade;
		float want = entityData.get(DARK) ? 0.14f : 1;
		if (phantasm() > 0)
			want = Math.min(want, 0.22f);
		fade += Mth.clamp(want - fade, -0.08f, 0.08f);
	}

	@Override
	protected void customServerAiStep() {
		super.customServerAiStep();

		if (tickCount % 5 == 0)
			entityData.set(DARK, level().getMaxLocalRawBrightness(BlockPos.containing(getEyePosition())) <= 7);

		if (meleeCd > 0) meleeCd--;
		if (skullCd > 0) skullCd--;
		if (wailCd > 0) wailCd--;

		if (tickCount % 20 == 0)
			prey = level().getNearestPlayer(TargetingConditions.forCombat().range(30).ignoreLineOfSight(), this);

		if (!valid(prey))
			prey = null;
		if (getTarget() != null && (!valid(getTarget()) || distanceToSqr(getTarget()) > 4096))
			setTarget(null);
		if (phase() == HUNTING && getTarget() == null && prey != null)
			setTarget(prey);

		if (getLastHurtByMob() != null)
			phaseTimer = Math.max(phaseTimer, 40);
		if (action == NONE && (prey != null || phase() == HUNTING) && --phaseTimer <= 0)
			shift();

		int ph = phantasm();
		if (ph > 0)
			entityData.set(PHANTASM, ph - 1);
		else if (!faded && phase() == HUNTING && getHealth() < getMaxHealth() / 3)
			vanish();

		stare();
		if (action != NONE)
			act();
	}

	void shift() {
		switch (phase()) {
			case WATCHING -> {
				entityData.set(PHASE, STALKING);
				phaseTimer = 400 + random.nextInt(400);
				creeping = false;
			}
			case STALKING -> {
				if (!creeping && prey != null) {
					creeping = true;
					phaseTimer = 300;
					return;
				}
				creeping = false;
				hunt(prey);
			}
			default -> {
				entityData.set(PHASE, WATCHING);
				phaseTimer = 200 + random.nextInt(300);
				fury = false;
				setTarget(null);
			}
		}
	}

	void hunt(LivingEntity t) {
		entityData.set(PHASE, HUNTING);
		phaseTimer = 400 + random.nextInt(600);
		if (valid(t))
			setTarget(t);
	}

	boolean valid(LivingEntity e) {
		return e != null && e.isAlive() && canAttack(e);
	}

	boolean lookedAt(Player p) {
		Vec3 to = new Vec3(getX() - p.getX(), getEyeY() - p.getEyeY(), getZ() - p.getZ());
		double d = to.length();
		if (d > 30 || d < 0.01)
			return false;
		return p.getViewVector(1).normalize().dot(to.scale(1 / d)) > 1 - 0.025 / d && p.hasLineOfSight(this);
	}

	void stare() {
		for (Player p : level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(30), EntitySelector.NO_CREATIVE_OR_SPECTATOR)) {
			if (!lookedAt(p)) {
				gaze.remove(p.getUUID());
				continue;
			}
			int n = gaze.merge(p.getUUID(), 1, Integer::sum);
			if (n % 10 == 1)
				p.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0, false, true), this);
			if (n > 60 && p.canFreeze())
				p.setTicksFrozen(Math.min(p.getTicksFrozen() + 3, p.getTicksRequiredToFreeze() + 20));
			if (n > 4 && phase() == STALKING && action == NONE) {
				prey = p;
				begin(SPOTTED, "spotted");
			}
		}
	}

	void go(double x, double y, double z, double speed) {
		moveControl.setWantedPosition(x, y, z, speed);
	}

	void halt() {
		moveControl.setWantedPosition(getX(), getY(), getZ(), 0);
	}

	void begin(int a, String anim) {
		action = a;
		actionTick = 0;
		halt();
		triggerAnim("main", anim);
	}

	void act() {
		int n = ++actionTick;
		LivingEntity t = getTarget();
		switch (action) {
			case SPOTTED -> {
				if (n >= 30) {
					hunt(prey);
					fury = true;
					action = NONE;
				}
			}
			case JUMPSCARE -> {
				if (n == 9 && prey != null && distanceToSqr(prey) < 25) {
					prey.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 60, 0), this);
					prey.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1), this);
				}
				if (n >= 53) {
					hunt(prey);
					action = NONE;
				}
			}
			case MELEE -> {
				if (n == 9 && t != null && distanceToSqr(t) < 12)
					doHurtTarget(t);
				if (n >= 26)
					action = NONE;
			}
			case SKULL -> {
				if (n == 16 && t != null)
					shoot(t);
				if (n >= 35)
					action = NONE;
			}
			case WAIL -> {
				if (n == 22)
					wail();
				if (n >= 68)
					action = NONE;
			}
			default -> action = NONE;
		}
	}

	void shoot(LivingEntity t) {
		Vec3 look = getViewVector(1);
		Vec3 from = new Vec3(getX() + look.x * 0.6 - look.z * 0.45, getY() + 2.1, getZ() + look.z * 0.6 + look.x * 0.45);
		Vec3 dir = new Vec3(t.getX() - from.x, t.getY(0.5) - from.y, t.getZ() - from.z).normalize();
		var skull = new BansheeSkull(level(), this, dir);
		skull.setPos(from);
		level().addFreshEntity(skull);
		playSound(ModSounds.BANSHEE_SKULL, 1.5f, 0.9f + random.nextFloat() * 0.2f);
	}

	void wail() {
		Vec3 look = getViewVector(1);
		for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class, getBoundingBox().inflate(9), e -> e != this && e.isAlive()
				&& !(e instanceof Banshee) && !(e instanceof BansheeDecoy) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(e))) {
			Vec3 to = e.position().subtract(position());
			double d = to.lengthSqr();
			if (d > 81 || d > 4 && look.dot(to.normalize()) < 0.3)
				continue;
			if (!e.hurt(damageSources().mobAttack(this), 14))
				continue;
			e.knockback(1.6, -to.x, -to.z);
			if (e.canFreeze())
				e.setTicksFrozen(Math.min(e.getTicksFrozen() + 120, e.getTicksRequiredToFreeze() + 40));
		}
	}

	void vanish() {
		faded = true;
		entityData.set(PHANTASM, 300);
		LivingEntity c = getTarget() != null ? getTarget() : this;
		for (int i = 0; i < 3; i++) {
			var d = ModEntities.BANSHEE_DECOY.create(level());
			if (d == null)
				continue;
			double a = random.nextDouble() * Math.PI * 2, r = 3 + random.nextDouble() * 4;
			d.moveTo(c.getX() + Math.cos(a) * r, getY() + random.nextDouble(), c.getZ() + Math.sin(a) * r, random.nextFloat() * 360, 0);
			d.owner = this;
			level().addFreshEntity(d);
		}
		playSound(ModSounds.BANSHEE_DISSOLVE, 1.5f, 0.7f);
	}

	@Override
	public boolean hurt(DamageSource src, float amt) {
		boolean ok = super.hurt(src, amt);
		if (ok && !level().isClientSide && phase() != HUNTING && src.getEntity() instanceof LivingEntity e && valid(e))
			hunt(e);
		return ok;
	}

	@Override
	public void die(DamageSource src) {
		if (!level().isClientSide)
			triggerAnim("main", "death");
		super.die(src);
	}

	@Override
	protected void tickDeath() {
		if (++deathTime >= 56 && !level().isClientSide && !isRemoved()) {
			level().broadcastEntityEvent(this, (byte) 60);
			remove(RemovalReason.KILLED);
		}
	}

	@Override
	public boolean causeFallDamage(float dist, float mult, DamageSource src) {
		return false;
	}

	@Override
	protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
	}

	@Override
	public boolean canFreeze() {
		return false;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return ModSounds.BANSHEE_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource src) {
		return ModSounds.BANSHEE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return null;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putInt("Phase", phase());
		tag.putInt("PhaseTime", phaseTimer);
		tag.putBoolean("Faded", faded);
	}

	@Override
	public void readAdditionalSaveData(CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		entityData.set(PHASE, Mth.clamp(tag.getInt("Phase"), WATCHING, HUNTING));
		phaseTimer = tag.getInt("PhaseTime");
		faded = tag.getBoolean("Faded");
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar reg) {
		reg.add(new AnimationController<>(this, "main", 5, s -> {
			if (isDeadOrDying())
				return s.setAndContinue(DEATH);
			return s.setAndContinue(s.isMoving() ? STALK : IDLE);
		})
				.triggerableAnim("spotted", play("spotted"))
				.triggerableAnim("jumpscare", play("scream_initiate"))
				.triggerableAnim("wail", play("scream_pierce"))
				.triggerableAnim("melee", play("attack"))
				.triggerableAnim("skull", play("ranged_attack"))
				.triggerableAnim("death", DEATH)
				.setSoundKeyframeHandler(e -> sound(e.getKeyframeData().getSound()))
				.setParticleKeyframeHandler(e -> particles(e.getKeyframeData().getEffect(), e.getKeyframeData().getLocator())));
	}

	static RawAnimation play(String name) {
		return RawAnimation.begin().thenPlay("animation.banshee." + name);
	}

	void sound(String name) {
		SoundEvent ev = switch (name) {
			case "spotted" -> ModSounds.BANSHEE_SPOTTED;
			case "scream_initiate" -> ModSounds.BANSHEE_SCREAM;
			case "scream_pierce_windup" -> ModSounds.BANSHEE_WINDUP;
			case "scream_pierce" -> ModSounds.BANSHEE_WAIL;
			case "death_shriek" -> ModSounds.BANSHEE_DEATH;
			case "dissolve" -> ModSounds.BANSHEE_DISSOLVE;
			case "attack_swipe" -> ModSounds.BANSHEE_SWIPE;
			default -> null;
		};
		if (ev != null)
			level().playLocalSound(getX(), getEyeY(), getZ(), ev, getSoundSource(), name.equals("scream_initiate") ? 2.5f : 1.5f, 0.9f + random.nextFloat() * 0.2f, false);
	}

	void particles(String effect, String locator) {
		double y = getY() + switch (locator == null ? "" : locator) {
			case "tail" -> 0.6;
			case "lower_body" -> 0.9;
			case "middle_body", "right_arm", "left_arm" -> 1.3;
			case "upper_body" -> 1.6;
			default -> 2.2;
		};
		switch (effect) {
			case "sonic_wave" -> {
				Vec3 look = getViewVector(1);
				for (int i = 1; i <= 6; i++)
					level().addParticle(ParticleTypes.SONIC_BOOM, getX() + look.x * i * 1.4, y + look.y * i, getZ() + look.z * i * 1.4, 0, 0, 0);
				burst(ParticleTypes.SNOWFLAKE, y, 30, 0.6);
			}
			case "dissolve" -> burst(ParticleTypes.SNOWFLAKE, y, 12, 0.15);
			case "dissolve_final" -> {
				burst(ParticleTypes.SNOWFLAKE, y, 40, 0.3);
				burst(ParticleTypes.POOF, y, 10, 0.1);
			}
			default -> {
			}
		}
	}

	void burst(ParticleOptions p, double y, int n, double speed) {
		for (int i = 0; i < n; i++)
			level().addParticle(p, getRandomX(0.6), y + random.nextGaussian() * 0.2, getRandomZ(0.6),
					random.nextGaussian() * speed, random.nextGaussian() * speed, random.nextGaussian() * speed);
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}

	class Act extends Goal {
		Act() {
			setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
		}

		@Override
		public boolean canUse() {
			return action != NONE;
		}

		@Override
		public void start() {
			halt();
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		@Override
		public void tick() {
			LivingEntity t = action == SPOTTED || action == JUMPSCARE ? prey : getTarget();
			if (t != null)
				getLookControl().setLookAt(t, 30, 30);
		}
	}

	class Hunt extends Goal {
		int flit, dash;

		Hunt() {
			setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
		}

		@Override
		public boolean canUse() {
			LivingEntity t = getTarget();
			return phase() == HUNTING && action == NONE && valid(t);
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		@Override
		public void stop() {
			dash = 0;
			halt();
		}

		@Override
		public void tick() {
			LivingEntity t = getTarget();
			if (!valid(t))
				return;
			getLookControl().setLookAt(t, 30, 30);
			double d = distanceToSqr(t);
			boolean sees = getSensing().hasLineOfSight(t);

			if (dash > 0) {
				dash--;
				go(t.getX(), t.getY(), t.getZ(), fury ? 1.15 : 0.9);
				if (d < 6 && meleeCd <= 0) {
					dash = 0;
					meleeCd = 25;
					begin(MELEE, "melee");
				}
				return;
			}
			if (d < 100 && sees && wailCd <= 0 && random.nextInt(80) == 0) {
				wailCd = 500;
				begin(WAIL, "wail");
				return;
			}
			if (d > 36 && d < 576 && sees && skullCd <= 0 && random.nextInt(20) == 0) {
				skullCd = 70 + random.nextInt(60);
				begin(SKULL, "skull");
				return;
			}
			if (d < 225 && meleeCd <= 0 && random.nextInt(fury ? 10 : 20) == 0) {
				dash = 30;
				return;
			}

			if (d > 144) {
				go(t.getX(), t.getY() + 1, t.getZ(), fury ? 0.8 : 0.6);
			} else if (--flit <= 0 || !moveControl.hasWanted()) {
				flit = 8 + random.nextInt(14);
				double a = random.nextDouble() * Math.PI * 2, r = 3 + random.nextDouble() * 3;
				go(t.getX() + Math.cos(a) * r, t.getY() + 1 + random.nextDouble() * 2, t.getZ() + Math.sin(a) * r, fury ? 0.7 : 0.5);
			}
		}
	}

	class Stalk extends Goal {
		int repath;

		Stalk() {
			setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
		}

		@Override
		public boolean canUse() {
			return phase() == STALKING && action == NONE && valid(prey);
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		@Override
		public void stop() {
			halt();
		}

		@Override
		public void tick() {
			if (prey == null)
				return;
			getLookControl().setLookAt(prey, 10, 10);

			Vec3 facing = prey.getLookAngle().multiply(1, 0, 1).normalize();
			Vec3 back = prey.position().subtract(facing.scale(creeping ? 1.6 : 9));

			if (creeping && distanceToSqr(back.x, prey.getY(), back.z) < 3) {
				Vec3 to = position().subtract(prey.position()).multiply(1, 0, 1).normalize();
				if (facing.dot(to) < -0.3) {
					creeping = false;
					begin(JUMPSCARE, "jumpscare");
					return;
				}
			}

			if (--repath <= 0) {
				repath = 10;
				go(back.x, prey.getY() + 0.3, back.z, creeping ? 0.55 : 0.35);
			}
		}
	}

	class Watch extends Goal {
		Watch() {
			setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
		}

		@Override
		public boolean canUse() {
			return phase() == WATCHING && action == NONE && valid(prey) && distanceToSqr(prey) < 576;
		}

		@Override
		public void start() {
			halt();
		}

		@Override
		public boolean requiresUpdateEveryTick() {
			return true;
		}

		@Override
		public void tick() {
			if (prey != null)
				getLookControl().setLookAt(prey, 10, 10);
		}
	}

	class Drift extends MoveControl {
		Drift() {
			super(Banshee.this);
		}

		@Override
		public void tick() {
			if (operation != Operation.MOVE_TO)
				return;
			Vec3 d = new Vec3(wantedX - getX(), wantedY - getY(), wantedZ - getZ());
			double len = d.length();
			if (len < 0.5 || speedModifier <= 0) {
				operation = Operation.WAIT;
				setDeltaMovement(getDeltaMovement().scale(0.5));
				return;
			}
			setDeltaMovement(getDeltaMovement().add(d.scale(speedModifier * getAttributeValue(Attributes.FLYING_SPEED) * 0.15 / len)));
			LivingEntity t = getTarget();
			Vec3 v = t != null && phase() == HUNTING ? t.position().subtract(position()) : getDeltaMovement();
			if (v.horizontalDistanceSqr() > 1e-4) {
				setYRot(-(float) Mth.atan2(v.x, v.z) * Mth.RAD_TO_DEG);
				yBodyRot = getYRot();
			}
		}
	}

	class Roam extends Goal {
		Roam() {
			setFlags(EnumSet.of(Flag.MOVE));
		}

		@Override
		public boolean canUse() {
			return action == NONE && !moveControl.hasWanted() && random.nextInt(60) == 0;
		}

		@Override
		public boolean canContinueToUse() {
			return false;
		}

		@Override
		public void start() {
			int x = Mth.floor(getX()) + random.nextInt(21) - 10;
			int z = Mth.floor(getZ()) + random.nextInt(21) - 10;
			int y = level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
			go(x + 0.5, y + 0.3 + random.nextDouble() * 1.5, z + 0.5, 0.2);
		}
	}
}
