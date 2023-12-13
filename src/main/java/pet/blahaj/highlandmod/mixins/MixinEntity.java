package pet.blahaj.highlandmod.mixins;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pet.blahaj.highlandmod.effectsystem.effects.EntityDespawner;
import pet.blahaj.highlandmod.effectsystem.effects.Passengers;
import pet.blahaj.highlandmod.effectsystem.effects.VelocityRandomizer;

@Mixin(Entity.class)
public abstract class MixinEntity {

    @Inject(at = @At("HEAD"), method = "tick()V")
    void tick(CallbackInfo ci) {
        VelocityRandomizer.tick((Entity)(Object)this);
        Passengers.tick((Entity)(Object)this);
    }


    @Inject(at = @At("TAIL"), method = "tick()V")
    void endTick(CallbackInfo ci) {
        EntityDespawner.tick((Entity)(Object)this);
    }
}
