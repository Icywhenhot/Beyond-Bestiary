package com.example.entity;

import com.example.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BansheeDecoy extends Mob implements GeoEntity {
	final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	Banshee owner;
	int flit;
	double angle, radius, turn;

	public BansheeDecoy(EntityType<? extends BansheeDecoy> type, Level level) {
		super(type, level);
		setNoGravity(true);
	}

	public static AttributeSupplier.Builder attrs() {
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 1);
	}

	@Override
	public void tick() {
		noPhysics = true;
		super.tick();
		noPhysics = false;
		if (level().isClientSide)
			return;
		if (owner == null || !owner.isAlive() || owner.phantasm() <= 0 || tickCount > 300) {
			poof();
			return;
		}
		LivingEntity t = owner.getTarget();
		if (!owner.valid(t))
			t = level().getNearestPlayer(this, 32);
		if (t == null) {
			setDeltaMovement(getDeltaMovement().scale(0.8));
			return;
		}
		getLookControl().setLookAt(t, 30, 30);
		if (tickCount == 1)
			angle = Mth.atan2(getZ() - t.getZ(), getX() - t.getX());
		if (--flit <= 0) {
			flit = 60 + random.nextInt(80);
			radius = 8 + random.nextDouble() * 5;
			turn = (random.nextBoolean() ? 1 : -1) * (0.02 + random.nextDouble() * 0.015);
		}
		angle += turn;
		Vec3 to = new Vec3(t.getX() + Math.cos(angle) * radius, t.getY() + 1 + Math.sin(tickCount * 0.08 + getId()) * 0.6,
				t.getZ() + Math.sin(angle) * radius).subtract(position());
		double len = to.length();
		Vec3 velocity = len > 0.01 ? to.scale(Math.min(0.35, len * 0.2) / len) : Vec3.ZERO;
		setDeltaMovement(getDeltaMovement().lerp(velocity, 0.3));
		setYRot(-((float) Mth.atan2(t.getX() - getX(), t.getZ() - getZ())) * Mth.RAD_TO_DEG);
		yBodyRot = getYRot();
	}

	@Override
	public boolean hurt(DamageSource src, float amt) {
		if (src.is(DamageTypeTags.IS_PROJECTILE))
			return false;
		if (!level().isClientSide && !isInvulnerableTo(src))
			poof();
		return true;
	}

	@Override
	public boolean canBeHitByProjectile() {
		return false;
	}

	void poof() {
		if (level() instanceof ServerLevel sl)
			sl.sendParticles(ParticleTypes.SNOWFLAKE, getX(), getEyeY(), getZ(), 15, 0.2, 0.2, 0.2, 0.03);
		playSound(ModSounds.BANSHEE_DISSOLVE, 0.8f, 1.4f);
		discard();
	}

	@Override
	public boolean isPushable() {
		return false;
	}

	@Override
	public boolean shouldBeSaved() {
		return false;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar reg) {
		reg.add(new AnimationController<>(this, "main", 5, s -> s.setAndContinue(getDeltaMovement().horizontalDistanceSqr() > 0.0001 ? Banshee.STALK : Banshee.IDLE)));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}
}
