package pet.blahaj.highlandmod.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pet.blahaj.highlandmod.effectsystem.effects.VelocityRandomizer;

@Mixin(FallingBlockEntity.class)
public abstract class MixinFallingBlockEntity {
    @Inject(at = @At("HEAD"), method = "tick()V")
    void tick(CallbackInfo ci) {
        VelocityRandomizer.tick((Entity)(Object)this);
    }
}
