package dev.amble.ait.data.schema.exterior.category;

import java.util.UUID;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.devteam.DevTeam;
import dev.amble.ait.data.schema.exterior.ExteriorCategorySchema;

/**
 * Used for dev exclusives.
 */
public class ExclusiveCategory extends ExteriorCategorySchema {
    public static final Identifier REFERENCE = AITMod.id("exterior/exclusive");

    public ExclusiveCategory() {
        super(REFERENCE, "exclusive");
    }

    public static boolean isUnlocked(UUID uuid) {
        return DevTeam.isDev(uuid);
    }
}
