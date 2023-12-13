package pet.blahaj.highlandmod.effectsystem.effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import pet.blahaj.highlandmod.effectsystem.EffectManager;
import pet.blahaj.highlandmod.effectsystem.EffectSettings;
import pet.blahaj.highlandmod.effectsystem.EffectTimer;
import pet.blahaj.highlandmod.effectsystem.HighlandEffect;

import java.util.HashMap;
import java.util.UUID;

public class EntityDespawner implements HighlandEffect {

    private static final HashMap<UUID, EffectTimer> entityEffects = new HashMap<>();
    private static boolean initialized = false;
    @Override
    public EffectSettings load_settings() {
        return new EffectSettings("EntityDespawner", 0.75f, 3f, 60f);
    }

    @Override
    public void initialize() {
        initialized = true;
    }


    public synchronized static void tick(Entity entity) {
        if(!initialized) return;
        if(entity instanceof PlayerEntity) return;
        EffectTimer timer = entityEffects.computeIfAbsent(entity.getUuid(), (uuid) -> EffectManager.new_timer("EntityDespawner"));
        if(timer.tick_and_check()) {
            entity.stopRiding();
            entity.removeAllPassengers();
            entity.teleport(entity.getX(), -1000, entity.getZ());
            entity.kill();
        }
    }
}
