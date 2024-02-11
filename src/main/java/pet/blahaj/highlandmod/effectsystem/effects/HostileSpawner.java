package pet.blahaj.highlandmod.effectsystem.effects;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import pet.blahaj.highlandmod.effectsystem.*;

public class HostileSpawner implements HighlandEffect {

    private EffectTimer hostileSpawnTimer;

    @Override
    public EffectSettings load_settings() {
        return new EffectSettings("HostileSpawner", 0.5f, 0.5f, 5f);
    }

    @Override
    public void initialize() {
        hostileSpawnTimer = EffectManager.new_timer("HostileSpawner");
        ServerTickEvents.START_WORLD_TICK.register((world) -> {
            if(hostileSpawnTimer.tick_and_check()) spawnMonster(world);
        });
    }

    private void spawnMonster(ServerWorld world) {

        int difficulty = (int) (Math.pow(EffectTimer.random(), 5.0) * 4.2);

        Entity entity = switch (difficulty) {
            case 0:
                yield switch (EffectTimer.random_int(12)) {
                    case 0 -> {
                        ZombieEntity zomb = EffectTimer.random_int(10) == 0 ? EntityType.ZOMBIE_VILLAGER.create(world) : EntityType.ZOMBIE.create(world);
                        zomb.setBaby(EffectTimer.random_int(100) == 0);
                        yield zomb;
                    }
                    case 1 -> EntityType.CREEPER.create(world);
                    case 2 -> EntityType.DROWNED.create(world);
                    case 3 -> EntityType.ENDERMITE.create(world);
                    case 4 -> EntityType.HUSK.create(world);
                    case 5 -> EntityType.PHANTOM.create(world);
                    case 6 -> EntityType.SILVERFISH.create(world);
                    case 7 -> {
                        SlimeEntity slime = EntityType.SLIME.create(world);
                        slime.setSize(EffectTimer.random_int(3) + 1, true);
                        if (EffectTimer.random_int(100) == 0) slime.setSize(5, true);
                        yield slime;
                    }
                    case 8 -> EntityType.SPIDER.create(world);
                    case 9 -> EntityType.ZOMBIFIED_PIGLIN.create(world);
                    case 10 -> EntityType.PIGLIN.create(world);
                    case 11 -> EntityType.ENDERMAN.create(world);
                    default -> throw new IllegalStateException("Unexpected easy monster");
                };
            case 1:
                yield switch (EffectTimer.random_int(7)) {
                    case 0 -> EntityType.WITHER_SKELETON.create(world);
                    case 1 -> EntityType.SKELETON.create(world);
                    case 2 -> {
                        MagmaCubeEntity slime = EntityType.MAGMA_CUBE.create(world);
                        slime.setSize(EffectTimer.random_int(3) + 1, true);
                        if (EffectTimer.random_int(100) == 0) slime.setSize(5, true);
                        yield slime;
                    }
                    case 3 -> EntityType.PILLAGER.create(world);
                    case 4 -> EntityType.STRAY.create(world);
                    case 5 -> EntityType.GHAST.create(world);
                    case 6 -> EntityType.GUARDIAN.create(world);
                    default -> throw new IllegalStateException("Unexpected medium monster");
                };
            case 2:
                yield switch (EffectTimer.random_int(6)) {
                    case 0 -> EntityType.SHULKER.create(world);
                    case 1 -> EntityType.WITCH.create(world);
                    case 2 -> EntityType.VEX.create(world);
                    case 3 -> EntityType.BLAZE.create(world);
                    case 4 -> EntityType.CAVE_SPIDER.create(world);
                    case 5 -> EntityType.HOGLIN.create(world);
                    default -> throw new IllegalStateException("Unexpected tricky monster");
                };
            case 3:
                yield switch (EffectTimer.random_int(6)) {
                    case 0 -> EntityType.ELDER_GUARDIAN.create(world);
                    case 1 -> EntityType.EVOKER.create(world);
                    case 2 -> EntityType.PIGLIN_BRUTE.create(world);
                    case 3 -> EntityType.RAVAGER.create(world);
                    case 4 -> EntityType.VINDICATOR.create(world);
                    case 5 -> EntityType.ZOGLIN.create(world);
                    default -> throw new IllegalStateException("Unexpected hard monster");
                };
            case 4:
                yield switch (EffectTimer.random_int(2)) {
                    case 0 -> EntityType.WARDEN.spawn(world, BlockPos.ORIGIN, SpawnReason.TRIGGERED);
                    case 1 -> EntityType.WITHER.create(world);
                    default -> throw new IllegalStateException("Unexpected boss");
                };
            default:
                throw new IllegalStateException("Unexpected difficulty: " + difficulty);
        };

        EffectUtils.updateSpawnPos(world, entity, 20, 5, true);
        if (world.getLightLevel(entity.getBlockPos()) > 8) {
            entity.discard();
            return;
        }
        world.spawnEntity(entity);
    }


}
