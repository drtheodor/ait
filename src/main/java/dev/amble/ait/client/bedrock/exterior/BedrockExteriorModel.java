package dev.amble.ait.client.bedrock.exterior;

import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.client.models.exteriors.ExteriorModel;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.client.bedrock.BedrockAnimationRegistry;
import dev.amble.ait.client.bedrock.BedrockModel;
import dev.amble.ait.core.tardis.handler.DoorHandler;
import dev.amble.ait.data.schema.door.AnimatedDoor;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.lib.api.Identifiable;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class BedrockExteriorModel implements ExteriorModel, Identifiable {
	private final BedrockModel model;
	private ModelPart root;

	public BedrockExteriorModel(BedrockModel model) {
		this.model = model;

		if (this.model == null) throw new IllegalStateException("Bedrock Model is null. Ensure the resource pack is loaded correctly.");

		this.root = this.model.create().createModel();
	}

	@Override
	public Identifier id() {
		return this.model.id();
	}

	@Override
	public void renderWithAnimations(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha, float tickDelta) {
		matrices.push();

		ExteriorVariantSchema schema = tardis.getExterior().getVariant();

		if (schema instanceof AnimatedDoor animDoor) {
			animDoor.runAnimations(root, matrices, tickDelta, tardis);
		}

		this.render(matrices, vertices, light, overlay, red, green, blue, alpha);

		matrices.pop();
	}

	@Override
	public <T extends Entity & Linkable> void renderEntity(T falling, ModelPart root, MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
		this.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
	}

	@Override
	public void renderDoors(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, boolean isBOTI) {

	}

	@Override
	public ModelPart getPart() {
		return this.root;
	}
}
