package pet.blahaj.highlandmod.effectsystem.effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import pet.blahaj.highlandmod.effectsystem.EffectManager;
import pet.blahaj.highlandmod.effectsystem.EffectSettings;
import pet.blahaj.highlandmod.effectsystem.EffectTimer;
import pet.blahaj.highlandmod.effectsystem.HighlandEffect;

import java.util.HashMap;
import java.util.UUID;

public class VelocityRandomizer implements HighlandEffect {

    private static final HashMap<UUID, EffectTimer> entityEffects = new HashMap<>();
    private static boolean initialized = false;
    @Override
    public EffectSettings load_settings() {
        return new EffectSettings("VelocityRandomizer", 0.5f, 0f, 2f);
    }

    @Override
    public void initialize() {
        initialized = true;
    }

    public synchronized static void tick(Entity entity) {
        if(!initialized) return;
        if(entity instanceof PlayerEntity) return;


        EffectTimer timer = entityEffects.computeIfAbsent(entity.getUuid(), (uuid) -> EffectManager.new_timer("VelocityRandomizer"));

        float modifier = (float) Math.max(timer.delayTime, 1) / (float) Math.max(timer.settings.maxDelayTicks, 1);

        if(!timer.tick_and_check()) return;


        float maxSpeed = entity instanceof LivingEntity ? 3 : (entity instanceof FallingBlockEntity ? 2 : 1);
        float speed = (float) (maxSpeed * modifier * Math.pow(EffectTimer.random(), 1.8));

        double x = EffectTimer.random(-speed, speed);
        double y = EffectTimer.random(-speed, speed) * 0.6;
        double z = EffectTimer.random(-speed, speed);
        entity.addVelocity(x, y, z);
    }
}
