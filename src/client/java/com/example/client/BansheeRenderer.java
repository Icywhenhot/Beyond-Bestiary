package com.example.client;

import com.example.BeyondBestiary;
import com.example.entity.Banshee;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.Color;

public class BansheeRenderer extends GeoEntityRenderer<Banshee> {
	public BansheeRenderer(EntityRendererProvider.Context ctx) {
		super(ctx, new DefaultedEntityGeoModel<>(BeyondBestiary.id("banshee")));
		addRenderLayer(new BansheeEyes<>(this, BansheeRenderer::eyes));
		shadowRadius = 0.5f;
	}

	static int eyes(Banshee b, float pt) {
		int rgb = switch (b.phase()) {
			case Banshee.WATCHING -> 0x9fd8ff;
			case Banshee.STALKING -> 0xffc24a;
			default -> 0xff3b3b;
		};
		if (b.phantasm() <= 0)
			return 0xff000000 | rgb;
		float k = 0.35f + 0.65f * (0.5f + 0.5f * Mth.sin((b.tickCount + pt) * 0.35f));
		return 0xff000000 | (int) ((rgb >> 16 & 255) * k) << 16 | (int) ((rgb >> 8 & 255) * k) << 8 | (int) ((rgb & 255) * k);
	}

	@Override
	public RenderType getRenderType(Banshee b, ResourceLocation tex, MultiBufferSource buffers, float pt) {
		return RenderType.entityTranslucent(tex);
	}

	@Override
	public Color getRenderColor(Banshee b, float pt, int light) {
		return Color.ofARGB((int) (Mth.lerp(pt, b.fadeO, b.fade) * 255), 255, 255, 255);
	}

	@Override
	protected float getDeathMaxRotation(Banshee b) {
		return 0;
	}

	@Override
	public void render(Banshee b, float yaw, float pt, PoseStack poseStack, MultiBufferSource buffers, int light) {
		shadowRadius = 0.5f * Mth.lerp(pt, b.fadeO, b.fade);
		super.render(b, yaw, pt, poseStack, buffers, light);
	}
}
