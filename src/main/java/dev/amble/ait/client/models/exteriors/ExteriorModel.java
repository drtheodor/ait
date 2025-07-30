package dev.amble.ait.client.models.exteriors;

import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.handler.DoorHandler;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public interface ExteriorModel {
	void renderWithAnimations(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices,
	                          VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha);

	<T extends Entity & Linkable> void renderEntity(T falling, ModelPart root, MatrixStack matrices,
	                                                VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha);

	void renderDoors(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices,
	                                 VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, boolean isBOTI);

	/**
	 * @return the root model part
	 */
	ModelPart getPart();

	default void render(MatrixStack stack, VertexConsumer buffer, int maxLightCoordinate, int defaultUv, float base, float base1, float base2, float v) {
		ModelPart root = getPart();
		root.render(stack, buffer, maxLightCoordinate, defaultUv, base, base1, base2, v);
	}
}
