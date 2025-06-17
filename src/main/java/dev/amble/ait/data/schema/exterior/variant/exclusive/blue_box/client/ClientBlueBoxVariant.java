package dev.amble.ait.data.schema.exterior.variant.exclusive.blue_box.client;

import org.joml.Vector3f;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.exteriors.ExteriorModel;
import dev.amble.ait.client.models.exteriors.exclusive.BlueBoxExteriorModel;
import dev.amble.ait.data.datapack.exterior.BiomeOverrides;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.variant.exclusive.blue_box.BlueBoxVariant;

public class ClientBlueBoxVariant extends ClientExteriorVariantSchema {

    protected static final String CATEGORY_PATH = "textures/blockentities/exteriors/exclusive/blue_box/";
    private final Identifier texturePath;

    protected ClientBlueBoxVariant(String variant) {
        super(BlueBoxVariant.REFERENCE.withSuffixedPath("/" + variant));
        this.texturePath = AITMod.id(CATEGORY_PATH + variant + ".png");
    }

    public ClientBlueBoxVariant() {
        this("default");
    }

    @Override
    public ExteriorModel model() {
        return new BlueBoxExteriorModel(BlueBoxExteriorModel.getTexturedModelData().createModel());
    }

    @Override
    public Identifier texture() {
        return texturePath;
    }

    @Override
    public Identifier emission() {
        return null;
    }

    @Override
    public Vector3f sonicItemTranslations() {
        return new Vector3f(0.845f, 1.125f, 1.05f);
    }

    @Override
    public BiomeOverrides overrides() {
        return null;
    }

}
