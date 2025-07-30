package dev.amble.ait.data.schema.door;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.ait.AITMod;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.MachineRecipeSchema;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EnumMap;
import java.util.concurrent.atomic.AtomicReference;

public class DatapackDoor extends DoorSchema {
	public static final Codec<DatapackDoor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.fieldOf("id").forGetter(DoorSchema::id),
			Identifier.CODEC.fieldOf("open_sound").forGetter(DatapackDoor::getOpenSoundId),
			Identifier.CODEC.fieldOf("close_sound").forGetter(DatapackDoor::getCloseSoundId),
			Identifier.CODEC.fieldOf("model").forGetter(DatapackDoor::getModelId),
			Codec.BOOL.fieldOf("is_double").forGetter(DoorSchema::isDouble),
			PortalOffsets.CODEC.optionalFieldOf("offsets", new PortalOffsets()).forGetter(DatapackDoor::getOffsets),
			Codec.BOOL.optionalFieldOf("isDatapack", true).forGetter(DatapackDoor::wasDatapack)
		).apply(instance, DatapackDoor::new)
	);

	protected final Identifier openSound;
	protected final Identifier closeSound;
	protected final Identifier model;
	protected final boolean isDouble;
	protected final PortalOffsets offsets;
	protected final boolean initiallyDatapack;

	public DatapackDoor(Identifier id, Identifier openSound, Identifier closeSound, Identifier model, boolean isDouble, PortalOffsets offsets, boolean initiallyDatapack) {
		super(id);

		this.openSound = openSound;
		this.closeSound = closeSound;
		this.model = model;
		this.isDouble = isDouble;
		this.offsets = offsets;
		this.initiallyDatapack = initiallyDatapack;
	}

	@Override
	public boolean isDouble() {
		return isDouble;
	}

	@Override
	public SoundEvent openSound() {
		return SoundEvent.of(getOpenSoundId());
	}

	@Override
	public SoundEvent closeSound() {
		return SoundEvent.of(getCloseSoundId());
	}

	public Identifier getOpenSoundId() {
		return openSound;
	}

	public Identifier getCloseSoundId() {
		return closeSound;
	}

	@Override
	public Vec3d adjustPortalPos(Vec3d pos, Direction direction) {
		return this.offsets.apply(direction, pos);
	}

	public PortalOffsets getOffsets() {
		return offsets;
	}

	public boolean wasDatapack() {
		return initiallyDatapack; // Datapack doors are always considered as such
	}

	public Identifier getModelId() {
		return model;
	}

	public static DatapackDoor fromInputStream(InputStream stream) {
		return fromJson(JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject());
	}

	public static DatapackDoor fromJson(JsonObject json) {
		AtomicReference<DatapackDoor> created = new AtomicReference<>();

		CODEC.decode(JsonOps.INSTANCE, json).get().ifLeft(recipe -> {
			created.set(recipe.getFirst());
		}).ifRight(err -> {
			created.set(null);
			AITMod.LOGGER.error("Error decoding datapack door type: {}", err);
		});

		return created.get();
	}

	public static class PortalOffsets extends EnumMap<Direction, Vec3d> {
		public static final Codec<PortalOffsets> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Vec3d.CODEC.optionalFieldOf("north", Vec3d.ZERO).forGetter(map -> ((PortalOffsets) map).get(Direction.NORTH)),
				Vec3d.CODEC.optionalFieldOf("south", Vec3d.ZERO).forGetter(map -> ((PortalOffsets) map).get(Direction.SOUTH)),
				Vec3d.CODEC.optionalFieldOf("east", Vec3d.ZERO).forGetter(map -> ((PortalOffsets) map).get(Direction.EAST)),
				Vec3d.CODEC.optionalFieldOf("west", Vec3d.ZERO).forGetter(map -> ((PortalOffsets) map).get(Direction.WEST))
			).apply(instance, PortalOffsets::new));

		public PortalOffsets() {
			super(Direction.class);
		}

		public PortalOffsets(Vec3d north, Vec3d south, Vec3d east, Vec3d west) {
			this();
			this.put(Direction.NORTH, north);
			this.put(Direction.SOUTH, south);
			this.put(Direction.EAST, east);
			this.put(Direction.WEST, west);
		}

		public Vec3d apply(Direction dir, Vec3d pos) {
			Vec3d offset = this.get(dir);
			if (offset == null) {
				return pos;
			}
			return pos.add(offset);
		}
	}
}
