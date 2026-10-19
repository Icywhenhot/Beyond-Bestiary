package com.example.client;

import com.example.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class BeyondBestiaryClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		EntityRendererRegistry.register(ModEntities.BANSHEE, BansheeRenderer::new);
		EntityRendererRegistry.register(ModEntities.BANSHEE_DECOY, BansheeDecoyRenderer::new);
		EntityRendererRegistry.register(ModEntities.BANSHEE_SKULL, BansheeSkullRenderer::new);
	}
}