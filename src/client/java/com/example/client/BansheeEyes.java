package com.example.client;

import com.example.BeyondBestiary;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.function.ToIntBiFunction;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class BansheeEyes<T extends GeoAnimatable> extends GeoRenderLayer<T> {
	static final ResourceLocation TEX = BeyondBestiary.id("textures/entity/banshee_eyes.png");

	final ToIntBiFunction<T, Float> tint;

	public BansheeEyes(GeoRenderer<T> renderer, ToIntBiFunction<T, Float> tint) {
		super(renderer);
		this.tint = tint;
	}

	@Override
	public void render(PoseStack poseStack, T animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
		RenderType eyes = RenderType.eyes(TEX);
		getRenderer().reRender(model, poseStack, bufferSource, animatable, eyes, bufferSource.getBuffer(eyes), partialTick,
				LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, tint.applyAsInt(animatable, partialTick));
	}
}
