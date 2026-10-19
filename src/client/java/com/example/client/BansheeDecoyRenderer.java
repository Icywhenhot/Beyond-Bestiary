package com.example.client;

import com.example.BeyondBestiary;
import com.example.entity.BansheeDecoy;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.Color;

public class BansheeDecoyRenderer extends GeoEntityRenderer<BansheeDecoy> {
	public BansheeDecoyRenderer(EntityRendererProvider.Context ctx) {
		super(ctx, new DefaultedEntityGeoModel<>(BeyondBestiary.id("banshee")));
		addRenderLayer(new BansheeEyes<>(this, (d, pt) -> 0xffff3b3b));
		shadowRadius = 0;
	}

	@Override
	public RenderType getRenderType(BansheeDecoy d, ResourceLocation tex, MultiBufferSource buffers, float pt) {
		return RenderType.entityTranslucent(tex);
	}

	@Override
	public Color getRenderColor(BansheeDecoy d, float pt, int light) {
		return Color.ofARGB(0, 255, 255, 255);
	}
}
