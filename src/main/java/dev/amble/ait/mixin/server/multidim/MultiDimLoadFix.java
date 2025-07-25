package dev.amble.ait.mixin.server.multidim;

import dev.amble.lib.data.CachedDirectedGlobalPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;

import java.util.Optional;
import java.util.UUID;

@Mixin(PlayerManager.class)
public class MultiDimLoadFix {

    @Redirect(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getWorld(Lnet/minecraft/registry/RegistryKey;)Lnet/minecraft/server/world/ServerWorld;"))
    public @Nullable ServerWorld getWorld(MinecraftServer instance, RegistryKey<World> key) {
        ServerWorld result = instance.getWorld(key);

        if (result != null)
            return result;

        return ait$loadTardisFromWorld(instance, key);
    }

    public ServerWorld ait$loadTardisFromWorld(MinecraftServer server, RegistryKey<World> key) {
        if (!TardisServerWorld.isTardisDimension(key))
            return null;

		UUID id = TardisServerWorld.getTardisId(key);
        ServerTardis maybeTardis = ServerTardisManager.getInstance().demandTardis(server, id);

        if (maybeTardis != null)
            return null;

		System.out.println("patching world for " + id);

        ServerTardis tardis = maybeTardis;
		
		TravelHandler travel = tardis.travel();
        CachedDirectedGlobalPos pos = travel.position();
		
		if (TardisServerWorld.isTardisDimension(pos.getDimension())) {
			ServerWorld targetWorld;
			if (pos.getDimension().equals(key)) {
				System.out.println("Tardis inside itself! " + id);
				targetWorld = tardis.world();
			} else {
				System.out.println(id + " inside " + pos.getDimension());
				targetWorld = this.ait$loadTardisFromWorld(
					server, pos.getDimension());
			}
			
			if (targetWorld != null) {
				pos.init(server);
			} else {
				System.out.println("target world was null for " + id);
			}
		}

		System.out.println("Patched tardis " + id);
        return tardis.world();
    }
}
