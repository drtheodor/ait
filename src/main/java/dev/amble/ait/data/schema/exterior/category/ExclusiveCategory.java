package dev.amble.ait.data.schema.exterior.category;

import dev.amble.ait.AITMod;
import dev.amble.ait.data.schema.exterior.ExteriorCategorySchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import net.minecraft.util.Identifier;

import java.util.Set;
import java.util.UUID;

/**
 * Used for dev exclusives.
 */
public class ExclusiveCategory extends ExteriorCategorySchema {
    public static final Identifier REFERENCE = AITMod.id("exterior/exclusive");

    public static final UUID LOQOR = UUID.fromString("ad504e7c-22a0-4b3f-94e3-5b6ad5514cb6");
    public static final UUID OURO = UUID.fromString("07e6b550-be92-4422-a269-345593df5a10");
    public static final UUID CLASSIC = UUID.fromString("ba21f64b-35e3-4b4f-b04c-9ceb814ad533");

    public static final Set<UUID> PLAYERS = Set.of(
            LOQOR, OURO, CLASSIC
    );

    public ExclusiveCategory() {
        super(REFERENCE, "exclusive");
    }

    public static boolean isUnlocked(UUID uuid) {
        return PLAYERS.contains(uuid);
    }
}
