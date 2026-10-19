package com.example;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
	public static final SoundEvent BANSHEE_AMBIENT = reg("entity.banshee.ambient");
	public static final SoundEvent BANSHEE_HURT = reg("entity.banshee.hurt");
	public static final SoundEvent BANSHEE_DEATH = reg("entity.banshee.death");
	public static final SoundEvent BANSHEE_SPOTTED = reg("entity.banshee.spotted");
	public static final SoundEvent BANSHEE_SCREAM = reg("entity.banshee.scream");
	public static final SoundEvent BANSHEE_WINDUP = reg("entity.banshee.windup");
	public static final SoundEvent BANSHEE_WAIL = reg("entity.banshee.wail");
	public static final SoundEvent BANSHEE_SWIPE = reg("entity.banshee.swipe");
	public static final SoundEvent BANSHEE_DISSOLVE = reg("entity.banshee.dissolve");
	public static final SoundEvent BANSHEE_SKULL = reg("entity.banshee.skull");

	static SoundEvent reg(String name) {
		var id = BeyondBestiary.id(name);
		return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
	}

	public static void init() {
	}
}
