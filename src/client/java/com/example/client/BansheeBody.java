package com.example.client;

import com.example.BeyondBestiary;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BansheeBody<T extends Entity & GeoAnimatable> extends GeoEntityRenderer<T> {
	static final RenderType GLOW = RenderType.create("banshee_glow", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, true,
			RenderType.CompositeState.builder()
					.setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
					.setTextureState(new RenderStateShard.TextureStateShard(BeyondBestiary.id("textures/entity/banshee.png"), false, false))
					.setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
					.setOverlayState(RenderStateShard.OVERLAY)
					.setCullState(RenderStateShard.NO_CULL)
					.setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
					.createCompositeState(false));

	static final RenderType ECTOPLASM = RenderType.create("banshee_ectoplasm", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, false, true,
			RenderType.CompositeState.builder()
					.setShaderState(RenderStateShard.RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER)
					.setTextureState(new RenderStateShard.TextureStateShard(BeyondBestiary.id("textures/entity/banshee.png"), false, false))
					.setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY)
					.setOverlayState(RenderStateShard.OVERLAY)
					.setCullState(RenderStateShard.NO_CULL)
					.setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
					.createCompositeState(false));

	public BansheeBody(EntityRendererProvider.Context ctx, GeoModel<T> model) {
		super(ctx, model);
	}

	@Override
	public void renderRecursively(PoseStack pose, T entity, GeoBone bone, RenderType type, MultiBufferSource buffers, VertexConsumer buffer, boolean rerender, float pt, int light, int overlay, int color) {
		if (!rerender) {
			String name = bone.getName();
			type = name.endsWith("_ectoplasm") ? ECTOPLASM : GLOW;
			buffer = buffers.getBuffer(type);
			light = LightTexture.FULL_BRIGHT;
			overlay = OverlayTexture.NO_OVERLAY;
			if (name.contains("_eye"))
				color |= 0xff000000;
		}
		super.renderRecursively(pose, entity, bone, type, buffers, buffer, rerender, pt, light, overlay, color);
	}
}
