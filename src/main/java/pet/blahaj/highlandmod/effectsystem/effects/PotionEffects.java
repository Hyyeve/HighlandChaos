package pet.blahaj.highlandmod.effectsystem.effects;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.random.Random;
import pet.blahaj.highlandmod.effectsystem.EffectManager;
import pet.blahaj.highlandmod.effectsystem.EffectSettings;
import pet.blahaj.highlandmod.effectsystem.EffectTimer;
import pet.blahaj.highlandmod.effectsystem.HighlandEffect;

public class PotionEffects implements HighlandEffect {
    EffectTimer timer;

    @Override
    public EffectSettings load_settings() {
        return new EffectSettings("PotionEffects", 0.5f, 10f, 80f);
    }

    @Override
    public void initialize() {
        timer = EffectManager.new_timer("PotionEffects");

        ServerTickEvents.START_WORLD_TICK.register((world -> {
            if(!timer.tick_and_check()) return;

            int players = world.getPlayers().size();
            if(players == 0) return;
            PlayerEntity player = world.getPlayers().get(EffectTimer.random_int(players));

            int duration =  (int)(Math.pow(EffectTimer.random(), 2) * 600 + 20);
            int level = EffectTimer.random_int(1, 9);
            StatusEffectInstance instance = new StatusEffectInstance(Registries.STATUS_EFFECT.getRandom(Random.create()).get().value(), duration, level);
            if(instance.getEffectType() == StatusEffects.INSTANT_DAMAGE) instance = new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1, (int) (level * 0.1));
            if(instance.getEffectType() == StatusEffects.INSTANT_HEALTH) instance = new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1, (int) (level * 0.5));
            if(instance.getEffectType() == StatusEffects.POISON) instance = new StatusEffectInstance(StatusEffects.POISON, (int) (0.1 * duration), (int) (level * 0.2));
            if(instance.getEffectType() == StatusEffects.WITHER) instance = new StatusEffectInstance(StatusEffects.WITHER, (int) (0.1 * duration), (int) (level * 0.2));
            if(instance.getEffectType() == StatusEffects.NAUSEA) instance = new StatusEffectInstance(StatusEffects.NAUSEA, (int) (0.5 * duration), level);
            if(instance.getEffectType() == StatusEffects.DARKNESS) instance = new StatusEffectInstance(StatusEffects.DARKNESS, (int) (0.5 * duration), level);
            if(instance.getEffectType() == StatusEffects.BLINDNESS) instance = new StatusEffectInstance(StatusEffects.BLINDNESS, (int) (0.5 * duration), level);
            if(instance.getEffectType() == StatusEffects.LEVITATION) instance = new StatusEffectInstance(StatusEffects.LEVITATION, (int) (0.5 * duration), level);
            if(instance.getEffectType() == StatusEffects.MINING_FATIGUE) instance = new StatusEffectInstance(StatusEffects.MINING_FATIGUE, (int) (0.5 * duration), (int) (level * 0.5));
            player.addStatusEffect(instance);
        }));
    }
}
