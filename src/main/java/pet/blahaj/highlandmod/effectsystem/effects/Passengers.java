package pet.blahaj.highlandmod.effectsystem.effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import pet.blahaj.highlandmod.effectsystem.EffectManager;
import pet.blahaj.highlandmod.effectsystem.EffectSettings;
import pet.blahaj.highlandmod.effectsystem.EffectTimer;
import pet.blahaj.highlandmod.effectsystem.HighlandEffect;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Passengers implements HighlandEffect {
    private static final HashMap<UUID, EffectTimer> entityEffects = new HashMap<>();
    private static boolean initialized = false;
    @Override
    public EffectSettings load_settings() {
        return new EffectSettings("Passengers", 0.5f, 10f, 60f);
    }

    @Override
    public void initialize() {
        initialized = true;
    }


    public synchronized static void tick(Entity entity) {
        if(!initialized) return;
        if(entity instanceof PlayerEntity || entity instanceof ItemEntity || entity instanceof WardenEntity || entity instanceof WitherEntity || entity instanceof EnderDragonEntity) return;
        if(entity.hasPassengers() || entity.hasVehicle()) return;
        EffectTimer timer = entityEffects.computeIfAbsent(entity.getUuid(), (uuid) -> EffectManager.new_timer("Passengers"));
        if(timer.tick_and_check()) {
            List<Entity> possibilities = entity.getWorld().getOtherEntities(entity, entity.getBoundingBox().expand(10), (ent) -> {
                return (ent instanceof MobEntity) && !ent.hasPassengers();
            });
            if (!possibilities.isEmpty()) entity.startRiding(possibilities.get(EffectTimer.random_int(possibilities.size())), true);
        }
    }
}
