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
import software.bernie.geckolib.util.Color;

public class BansheeRenderer extends BansheeBody<Banshee> {
	public BansheeRenderer(EntityRendererProvider.Context ctx) {
		super(ctx, new DefaultedEntityGeoModel<>(BeyondBestiary.id("banshee")));
		shadowRadius = 0.5f;
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
