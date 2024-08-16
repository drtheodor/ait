package loqor.ait.core.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import loqor.ait.AITMod;
import loqor.ait.core.commands.argument.TardisArgumentType;
import loqor.ait.tardis.Tardis;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TravelDebugCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID)
                .then(literal("travel").requires(source -> source.hasPermissionLevel(2))
                        .then(argument("tardis", TardisArgumentType.tardis())
                                .then(literal("demat").executes(TravelDebugCommand::demat))
                                .then(literal("destination").then(argument("dimension", DimensionArgumentType.dimension())
                                        .then(argument("pos", BlockPosArgumentType.blockPos()).executes(TravelDebugCommand::setPos))))
                                .then(literal("remat").executes(TravelDebugCommand::remat))
                        )
                )
        );
    }

    private static int demat(CommandContext<ServerCommandSource> context) {
        Tardis tardis = TardisArgumentType.getTardis(context, "tardis");
        tardis.travel().dematerialize();

        return Command.SINGLE_SUCCESS;
    }

    private static int setPos(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        Tardis tardis = TardisArgumentType.getTardis(context, "tardis");
        ServerWorld world = DimensionArgumentType.getDimensionArgument(context, "dimension");
        BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");

        tardis.travel().destination(cached -> cached.world(world).pos(pos));
        return Command.SINGLE_SUCCESS;
    }

    private static int remat(CommandContext<ServerCommandSource> context) {
        Tardis tardis = TardisArgumentType.getTardis(context, "tardis");
        tardis.travel().rematerialize();

        return Command.SINGLE_SUCCESS;
    }
}
