package dex.mapwriter3.mixin;

import dex.mapwriter3.events.PlayerDeathCallback;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    //todo test
    @Inject(method = "onDeath(Lnet/minecraft/entity/damage/DamageSource;)V", at = @At("INVOKE"))
    private void playerDeath(DamageSource source, CallbackInfo ci) {
        PlayerDeathCallback.EVENT.invoker().death((PlayerEntity)(Object)this);
    }

}
