package com.example.entity;

import com.example.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.util.GeckoLibUtil;

public class BansheeDecoy extends Mob implements GeoEntity {
	final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	Banshee owner;

	public BansheeDecoy(EntityType<? extends BansheeDecoy> type, Level level) {
		super(type, level);
		setNoGravity(true);
	}

	public static AttributeSupplier.Builder attrs() {
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 1);
	}

	@Override
	public void tick() {
		super.tick();
		if (level().isClientSide)
			return;
		if (owner == null || !owner.isAlive() || owner.phantasm() <= 0 || tickCount > 300) {
			poof();
			return;
		}
		Player p = level().getNearestPlayer(this, 32);
		if (p != null)
			getLookControl().setLookAt(p, 10, 10);
		setDeltaMovement(Math.sin(tickCount * 0.05 + getId()) * 0.02, Math.sin(tickCount * 0.1) * 0.01, Math.cos(tickCount * 0.05 + getId()) * 0.02);
	}

	@Override
	public boolean hurt(DamageSource src, float amt) {
		if (!level().isClientSide && !isInvulnerableTo(src))
			poof();
		return true;
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
		reg.add(new AnimationController<>(this, "main", 5, s -> s.setAndContinue(Banshee.IDLE)));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return cache;
	}
}
