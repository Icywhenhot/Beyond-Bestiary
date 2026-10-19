package com.example;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

public class ModItems {
	public static final Item FROZEN_WISP = reg("frozen_wisp", new Item(new Item.Properties()));
	public static final Item BANSHEE_SPAWN_EGG = reg("banshee_spawn_egg", new SpawnEggItem(ModEntities.BANSHEE, 0xd6ecf7, 0x6fb3e0, new Item.Properties()));

	static Item reg(String name, Item item) {
		return Registry.register(BuiltInRegistries.ITEM, BeyondBestiary.id(name), item);
	}

	public static void init() {
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(e -> e.accept(FROZEN_WISP));
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(e -> e.accept(BANSHEE_SPAWN_EGG));
	}
}
