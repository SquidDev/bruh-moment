package net.dblsaiko.bruhmoment.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.dblsaiko.bruhmoment.util.Util;

// we need to beat fabric api calling the interact event
@Mixin(targets = "net/minecraft/server/network/ServerPlayNetworkHandler$1", priority = 999)
public class ServerPlayNetworkHandlerMixin {
    @Shadow
    public ServerPlayNetworkHandler field_28963;

    @Shadow
    public Entity field_28962;

    @Inject(
        method = "interact(Lnet/minecraft/util/Hand;)V",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    public void bmInteract(Hand hand, CallbackInfo ci) {
        if (!Util.canInteract(this.field_28963.player, this.field_28962, hand)) {
            ci.cancel();
        }
    }

    @Inject(
        method = "interactAt(Lnet/minecraft/util/Hand;Lnet/minecraft/util/math/Vec3d;)V",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    public void bmInteractAt(Hand hand, Vec3d pos, CallbackInfo ci) {
        if (!Util.canInteract(this.field_28963.player, this.field_28962, hand)) {
            ci.cancel();
        }
    }

    @Inject(
        method = "attack()V",
        at = @At(value = "HEAD"),
        cancellable = true
    )
    public void bmAttack(CallbackInfo ci) {
        if (!Util.canAttack(this.field_28963.player, this.field_28962)) {
            ci.cancel();
        }
    }
}
