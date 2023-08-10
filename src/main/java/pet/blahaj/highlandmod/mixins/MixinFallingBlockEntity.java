package pet.blahaj.highlandmod.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(FallingBlockEntity.class)
public abstract class MixinFallingBlockEntity {

    @Unique
    Random random = new Random();

    @Unique
    double cost = 1;

    @Unique
    int delay = 0;

    @Inject(at = @At("HEAD"), method = "tick()V")
    void tick(CallbackInfo ci) {

        if (random.nextInt(1500) == 0) ((Entity) ((Object) this)).discard();

        if (delay > 0) {
            delay--;
            return;
        }

        double maxSpeed = 2;
        double speed = maxSpeed;

        for (int i = 0; i < 3; i++) speed = Math.min(speed, random.nextDouble() * maxSpeed);

        speed *= cost;
        double newCost = 1 - random.nextDouble(speed / maxSpeed);
        cost = Math.min(newCost, cost + 0.01);

        delay = random.nextInt((int) (Math.pow(speed, 1.8) * 50) + 1) - 1;

        double x = random.nextDouble(-speed, speed);
        double y = random.nextDouble(-speed, speed) * 0.6;
        double z = random.nextDouble(-speed, speed);
        ((Entity) ((Object) this)).addVelocity(x, y, z);
    }
}
