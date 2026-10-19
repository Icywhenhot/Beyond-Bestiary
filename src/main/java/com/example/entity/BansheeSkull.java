package com.example.entity;

import com.example.ModEntities;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BansheeSkull extends AbstractHurtingProjectile implements GeoEntity {
	final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

	public BansheeSkull(EntityType<? extends BansheeSkull> type, Level level) {
		super(type, level);
	}

	public BansheeSkull(Level level, LivingEntity owner, Vec3 dir) {
		super(ModEntities.BANSHEE_SKULL, owner, dir, level);
	}

	@Override
	public void tick() {
		super.tick();
		if (!level().isClientSide && tickCount > 100)
			discard();
	}

	@Override
	protected void onHit(HitResult res) {
		super.onHit(res);
		if (!(level() instanceof ServerLevel sl))
			return;
		LivingEntity owner = getOwner() instanceof LivingEntity l ? l : null;
		Vec3 pos = res.getLocation();
		for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class, new AABB(pos, pos).inflate(2), e -> e != owner && e.isAlive()
				&& !(e instanceof Banshee) && !(e instanceof BansheeDecoy) && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(e))) {
			if (e.getBoundingBox().distanceToSqr(pos) > 4 || !e.hurt(damageSources().mobProjectile(this, owner), 6))
				continue;
			e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1), owner);
			if (e.canFreeze())
				e.setTicksFrozen(Math.min(e.getTicksFrozen() + 80, e.getTicksRequiredToFreeze() + 20));
		}
		sl.sendParticles(ParticleTypes.SNOWFLAKE, pos.x, pos.y, pos.z, 60, 0.8, 0.5, 0.8, 0.08);
		sl.sendParticles(ParticleTypes.POOF, pos.x, pos.y, pos.z, 8, 0.5, 0.3, 0.5, 0.02);
		playSound(SoundEvents.GLASS_BREAK, 1.2f, 0.9f);
		discard();
	}

	@Override
	protected boolean shouldBurn() {
		return false;
	}

	@Override
	protected ParticleOptions getTrailParticle() {
		return ParticleTypes.SNOWFLAKE;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar reg) {
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}
}
