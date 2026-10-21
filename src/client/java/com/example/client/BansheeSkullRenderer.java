package com.example.client;

import com.example.BeyondBestiary;
import com.example.entity.BansheeSkull;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.model.GeoModel;

public class BansheeSkullRenderer extends BansheeBody<BansheeSkull> {
	public BansheeSkullRenderer(EntityRendererProvider.Context ctx) {
		super(ctx, new GeoModel<>() {
			@Override
			public ResourceLocation getModelResource(BansheeSkull s) {
				return BeyondBestiary.id("geo/entity/banshee_skull.geo.json");
			}

			@Override
			public ResourceLocation getTextureResource(BansheeSkull s) {
				return BeyondBestiary.id("textures/entity/banshee.png");
			}

			@Override
			public ResourceLocation getAnimationResource(BansheeSkull s) {
				return BeyondBestiary.id("animations/entity/banshee.animation.json");
			}
		});
	}

	@Override
	public RenderType getRenderType(BansheeSkull s, ResourceLocation tex, MultiBufferSource buffers, float pt) {
		return RenderType.entityTranslucent(tex);
	}

	@Override
	protected void applyRotations(BansheeSkull s, PoseStack poseStack, float age, float yaw, float pt, float scale) {
		poseStack.mulPose(Axis.YP.rotationDegrees(180 + Mth.rotLerp(pt, s.yRotO, s.getYRot())));
		poseStack.mulPose(Axis.XP.rotationDegrees(-Mth.lerp(pt, s.xRotO, s.getXRot())));
	}
}
