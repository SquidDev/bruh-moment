package net.dblsaiko.bruhmoment.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.dblsaiko.bruhmoment.util.Util;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow @Final private MinecraftServer server;

    @Shadow public ServerPlayerEntity player;

    @Inject(
        method = "onPlayerInteractEntity(Lnet/minecraft/network/packet/c2s/play/PlayerInteractEntityC2SPacket;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayerEntity;squaredDistanceTo(Lnet/minecraft/entity/Entity;)D",
            shift = Shift.BEFORE
        ),
        cancellable = true
    )
    private void onInteract(PlayerInteractEntityC2SPacket rpacket, CallbackInfo ci) {
        ServerWorld serverWorld = this.player.getServerWorld();
        Entity entity = rpacket.getEntity(serverWorld);
        switch(rpacket.getType()) {
            case INTERACT:
            case INTERACT_AT:
                if (!Util.canInteract(this.player, entity, rpacket.getHand())) {
                    ci.cancel();
                }
                break;
            case ATTACK:
                if (!Util.canAttack(this.player, entity)) {
                    ci.cancel();
                }
                break;
        }
    }

}
