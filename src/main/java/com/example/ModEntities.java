package com.example;

import com.example.entity.Banshee;
import com.example.entity.BansheeDecoy;
import com.example.entity.BansheeSkull;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBiomeTags;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

public class ModEntities {
	public static final EntityType<Banshee> BANSHEE = reg("banshee", EntityType.Builder.of(Banshee::new, MobCategory.MONSTER)
			.sized(1f, 2.6f).eyeHeight(2.15f).clientTrackingRange(10));
	public static final EntityType<BansheeDecoy> BANSHEE_DECOY = reg("banshee_decoy", EntityType.Builder.of(BansheeDecoy::new, MobCategory.MISC)
			.sized(1f, 2.6f).eyeHeight(2.15f).clientTrackingRange(10));
	public static final EntityType<BansheeSkull> BANSHEE_SKULL = reg("banshee_skull", EntityType.Builder.<BansheeSkull>of(BansheeSkull::new, MobCategory.MISC)
			.sized(0.5f, 0.5f).clientTrackingRange(4).updateInterval(10));

	static <T extends Entity> EntityType<T> reg(String name, EntityType.Builder<T> b) {
		return Registry.register(BuiltInRegistries.ENTITY_TYPE, BeyondBestiary.id(name), b.build(name));
	}

	public static void init() {
		FabricDefaultAttributeRegistry.register(BANSHEE, Banshee.attrs());
		FabricDefaultAttributeRegistry.register(BANSHEE_DECOY, BansheeDecoy.attrs());

		SpawnPlacements.register(BANSHEE, SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Banshee::canSpawn);
		BiomeModifications.addSpawn(BiomeSelectors.tag(ConventionalBiomeTags.IS_SNOWY), MobCategory.MONSTER, BANSHEE, 15, 1, 1);
	}
}
