package dev.amble.ait.data.properties;

import java.util.HashSet;
import java.util.UUID;
import java.util.function.Function;

import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.data.DirectedGlobalPos;
import org.joml.Vector2i;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import dev.amble.ait.api.tardis.KeyedTardisComponent;

public class Property<T> {

    private final PropertyType<T> type;
    private final String name;

    protected final Function<KeyedTardisComponent, T> def;

    public Property(PropertyType<T> type, String name, Function<KeyedTardisComponent, T> def) {
        this.type = type;
        this.name = name;
        this.def = def;
    }

    public Property(PropertyType<T> type, String name, T def) {
        this(type, name, o -> def);
    }

    public Property(PropertyType.Nullable<T> type, String name) {
        this(type, name, o -> null);
    }

    public Value<T> create(KeyedTardisComponent holder) {
        T t = this.def == null ? null : this.def.apply(holder);
        Value<T> result = this.create(t);

        result.of(holder, this);
        return result;
    }

    protected Value<T> create(T t) {
        return new Value<>(t);
    }

    public String getName() {
        return name;
    }

    public PropertyType<T> getType() {
        return type;
    }

    public Property<T> copy(String name) {
        return new Property<>(this.type, name, this.def);
    }

    public Property<T> copy(String name, T def) {
        return new Property<>(this.type, name, def);
    }

    public static <T extends Enum<T>> Property<T> forEnum(String name, Class<T> clazz, T def) {
        return new Property<>(PropertyType.forEnum(clazz), name, def);
    }

    public static final PropertyType<DirectedGlobalPos> DIRECTED_GLOBAL_POS = new PropertyType<>(DirectedGlobalPos.class,
            (buf, pos) -> pos.write(buf), DirectedGlobalPos::read);

    public static final PropertyType<BlockPos> BLOCK_POS = new PropertyType<>(BlockPos.class, PacketByteBuf::writeBlockPos,
            PacketByteBuf::readBlockPos);

    public static final PropertyType<CachedDirectedGlobalPos> CDIRECTED_GLOBAL_POS = new PropertyType<>(
            CachedDirectedGlobalPos.class, (buf, pos) -> pos.write(buf), CachedDirectedGlobalPos::read);

    public static final PropertyType<Identifier> IDENTIFIER = new PropertyType<>(Identifier.class, PacketByteBuf::writeIdentifier,
            PacketByteBuf::readIdentifier);

    public static final PropertyType<Long> LONG = new PropertyType<>(Long.class, PacketByteBuf::writeLong, PacketByteBuf::readLong);

    public static final PropertyType<RegistryKey<World>> WORLD_KEY = new PropertyType<>(RegistryKey.class,
            PacketByteBuf::writeRegistryKey, buf -> buf.readRegistryKey(RegistryKeys.WORLD));

    public static final PropertyType<Direction> DIRECTION = PropertyType.forEnum(Direction.class);

    public static final PropertyType<Vector2i> VEC2I = new PropertyType<>(Vector2i.class, (buf, vector2i) -> {
        buf.writeInt(vector2i.x);
        buf.writeInt(vector2i.y);
    }, buf -> new Vector2i(buf.readInt(), buf.readInt()));

    public static final PropertyType<String> STR = new PropertyType<>(String.class, PacketByteBuf::writeString,
            PacketByteBuf::readString);

    public static final PropertyType.Nullable<UUID> UUID = new PropertyType.Nullable<>(UUID.class, PacketByteBuf::writeUuid, PacketByteBuf::readUuid);

    public static final PropertyType<Double> DOUBLE = new PropertyType<>(Double.class, PacketByteBuf::writeDouble,
            PacketByteBuf::readDouble);

    public static final PropertyType<HashSet<String>> STR_SET = new PropertyType<>(HashSet.class, (buf, strings) -> {
        buf.writeVarInt(strings.size());

        for (String str : strings)
            buf.writeString(str);
    }, buf -> {
        HashSet<String> result = new HashSet<>();
        int size = buf.readVarInt();

        for (int i = 0; i < size; i++) {
            result.add(buf.readString());
        }

        return result;
    });

    public static final PropertyType<ItemStack> ITEM_STACK = new PropertyType<>(ItemStack.class, PacketByteBuf::writeItemStack,
            PacketByteBuf::readItemStack);
}
