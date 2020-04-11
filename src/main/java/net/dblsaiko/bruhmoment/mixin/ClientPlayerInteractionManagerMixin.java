package net.dblsaiko.bruhmoment.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dblsaiko.bruhmoment.Util;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Shadow @Final private ClientPlayNetworkHandler networkHandler;

    @Inject(
        method = "interactEntity(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;interact(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
            shift = Shift.BEFORE
        ),
        cancellable = true
    )
    private void onInteract(PlayerEntity player, Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!Util.canInteract(player, entity, hand)) {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

    @Inject(
        method = "interactEntityAtLocation(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/hit/EntityHitResult;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;interactAt(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
            shift = Shift.BEFORE
        ),
        cancellable = true
    )
    private void onInteract(PlayerEntity player, Entity entity, EntityHitResult hitResult, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!Util.canInteract(player, entity, hand)) {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

    @Inject(
        method = "attackEntity(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/PlayerEntity;attack(Lnet/minecraft/entity/Entity;)V",
            shift = Shift.BEFORE
        ),
        cancellable = true
    )
    private void onAttack(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (!Util.canAttack(player, target)) {
            ci.cancel();
        }
    }

    @Inject(
        method = "interactItem(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/World;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/TypedActionResult;",
            shift = Shift.BEFORE
        ),
        cancellable = true
    )
    private void onUseItem(PlayerEntity player, World world, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (!Util.canUse(player, hand)) {
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

    @Inject(
        method = "interactBlock(Lnet/minecraft/client/network/ClientPlayerEntity;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;getStackInHand(Lnet/minecraft/util/Hand;)Lnet/minecraft/item/ItemStack;",
            shift = Shift.BEFORE
        ),
        cancellable = true
    )
    private void onInteract(ClientPlayerEntity player, ClientWorld world, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        BlockPos pos = hitResult.getBlockPos();
        BlockState state = world.getBlockState(pos);
        if (!Util.canInteract(player, state, hand)) {
            networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(hand, hitResult));
            cir.setReturnValue(ActionResult.FAIL);
        }
    }

}
