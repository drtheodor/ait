package dev.amble.ait.core.tardis.handler;

import java.util.Optional;

import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.drtheo.gaslighter.Gaslighter3000;
import dev.drtheo.gaslighter.api.FakeBlockEvents;

import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.schema.exterior.variant.adaptive.AdaptiveVariant;

public class ChameleonHandler extends TardisComponent {

    @Exclude
    private Gaslighter3000 gaslighter;

    static {
        TardisEvents.ENTER_FLIGHT.register(tardis -> {
            tardis.chameleon().clearDisguise();
        });

        TardisEvents.START_FALLING.register(tardis -> {
            tardis.chameleon().clearDisguise();
        });

        TardisEvents.TOGGLE_SIEGE.register((tardis, active) -> {
            if (shouldNotBeDisguised(tardis)) {
                tardis.chameleon().clearDisguise();
            } else {
                tardis.chameleon().applyDisguise();
            }
        });

        TardisEvents.LANDED.register(tardis -> {
            if (!shouldNotBeDisguised(tardis))
                tardis.chameleon().applyDisguise();
        });

        TardisEvents.SEND_TARDIS.register((tardis, player) -> {
            if (player.isInTeleportationState())
                return;

            if (shouldNotBeDisguised(tardis))
                return;

            CachedDirectedGlobalPos pos = tardis.travel().position();

            if (pos == null || pos.getWorld() != player.getServerWorld())
                return;

            tardis.chameleon().applyDisguise(player);
        });

        TardisEvents.EXTERIOR_CHANGE.register(tardis -> {
            if (shouldNotBeDisguised(tardis)) {
                tardis.chameleon().clearDisguise();
            } else {
                tardis.chameleon().applyDisguise();
            }
        });

        TardisEvents.DOOR_USED.register((tardis,  player) -> {
            if (player == null || !isDisguised(tardis))
                return DoorHandler.InteractionResult.CONTINUE;

            if (!shouldNotBeDisguised(tardis)) {
                tardis.chameleon().applyDisguise(player);
                return DoorHandler.InteractionResult.CONTINUE;
            }

            CachedDirectedGlobalPos cached = tardis.travel().position();
            Optional<ExteriorBlockEntity> blockEntity = tardis.getExterior().findExteriorBlock();

            if (blockEntity.isEmpty())
                return DoorHandler.InteractionResult.CONTINUE;

            player.networkHandler.sendPacket(new BlockUpdateS2CPacket(cached.getWorld(), cached.getPos()));
            player.networkHandler.sendPacket(new BlockUpdateS2CPacket(cached.getWorld(), cached.getPos().up()));
            player.networkHandler.sendPacket(BlockEntityUpdateS2CPacket.create(blockEntity.get()));

            return DoorHandler.InteractionResult.CONTINUE;
        });

        FakeBlockEvents.CHECK.register((player, state, pos) -> {
            if (state.isOf(AITBlocks.EXTERIOR_BLOCK))
                return;

            shitParticles(player.getServerWorld(), pos);
            player.networkHandler.sendPacket(new BlockUpdateS2CPacket(player.getServerWorld(), pos));
        });

        FakeBlockEvents.PLACED.register((world, state, pos) -> shitParticles(world, pos));
        FakeBlockEvents.REMOVED.register(ChameleonHandler::shitParticles);
    }

    public ChameleonHandler() {
        super(Id.CHAMELEON);
    }

    private static boolean shouldNotBeDisguised(Tardis tardis) {
        return !isDisguised(tardis) || tardis.flight().falling().get()
                || tardis.siege().isActive() || tardis.door().isOpen();
    }

    private static boolean isDisguised(Tardis tardis) {
        return tardis.getExterior().getVariant() instanceof AdaptiveVariant;
    }

    public void clearDisguise() {
        if (this.gaslighter == null)
            return;

        gaslighter.touchGrass();
        gaslighter.tweet();

        this.gaslighter = null;
    }

    /**
     * @return Whether the recalculation was successful
     */
    public boolean recalcDisguise() {
        if (this.gaslighter != null)
            return true;

        long start = System.currentTimeMillis();
        CachedDirectedGlobalPos cached = tardis.travel().position();
        ServerWorld world = cached.getWorld();

        this.gaslighter = tardis.<BiomeHandler>handler(Id.BIOME).testBiome(world, cached.getPos());

        if (this.gaslighter == null)
            return false;

        AITMod.LOGGER.debug("Recalculated exterior in {}ms", System.currentTimeMillis() - start);
        return true;
    }

    private void applyDisguise(ServerPlayerEntity player) {
        if (!this.recalcDisguise())
            return;

        this.gaslighter.tweet(player);
    }

    private void applyDisguise() {
        if (!this.recalcDisguise())
            return;

        this.gaslighter.tweet();
    }

    private static void shitParticles(ServerWorld world, BlockPos pos) {
        Vec3d center = pos.toCenterPos();
        world.spawnParticles(ParticleTypes.END_ROD, center.getX(), center.getY(), center.getZ(), 12, 0.3, 0.3, 0.3, 0);
    }
}
