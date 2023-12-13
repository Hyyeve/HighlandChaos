package pet.blahaj.highlandmod.effectsystem.effects;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.world.ServerWorld;
import pet.blahaj.highlandmod.effectsystem.*;

public class PassiveSpawner implements HighlandEffect {

    private EffectTimer passiveSpawnTimer;

    @Override
    public EffectSettings load_settings() {
        return new EffectSettings("PassiveSpawner", 0.7f, 1f, 7f);
    }

    @Override
    public void initialize() {
        passiveSpawnTimer = EffectManager.new_timer("PassiveSpawner");
        ServerTickEvents.START_WORLD_TICK.register((world) -> {
            if(passiveSpawnTimer.tick_and_check()) spawnPassive(world);
        });
    }

    private void spawnPassive(ServerWorld world) {
        Entity entity = switch (EffectTimer.random_int(39)) {
            case 0 -> EntityType.ALLAY.create(world);
            case 1 -> EntityType.AXOLOTL.create(world);
            case 2 -> EntityType.BAT.create(world);
            case 3 -> EntityType.CAMEL.create(world);
            case 4 -> EntityType.CAT.create(world);
            case 5 -> EntityType.CHICKEN.create(world);
            case 6 -> EntityType.COD.create(world);
            case 7 -> EntityType.COW.create(world);
            case 8 -> EntityType.DONKEY.create(world);
            case 9 -> EntityType.FOX.create(world);
            case 10 -> EntityType.FROG.create(world);
            case 11 -> EntityType.GOAT.create(world);
            case 12 -> EntityType.HORSE.create(world);
            case 13 -> EntityType.MOOSHROOM.create(world);
            case 14 -> EntityType.MULE.create(world);
            case 15 -> EntityType.OCELOT.create(world);
            case 16 -> EntityType.PANDA.create(world);
            case 17 -> EntityType.PARROT.create(world);
            case 18 -> EntityType.PIG.create(world);
            case 19 -> EntityType.POLAR_BEAR.create(world);
            case 20 -> EntityType.PUFFERFISH.create(world);
            case 21 -> EntityType.RABBIT.create(world);
            case 22 -> EntityType.SALMON.create(world);
            case 23 -> EntityType.SHEEP.create(world);
            case 24 -> EntityType.SNOW_GOLEM.create(world);
            case 25 -> EntityType.SQUID.create(world);
            case 26 -> EntityType.STRIDER.create(world);
            case 27 -> EntityType.TROPICAL_FISH.create(world);
            case 28 -> EntityType.TURTLE.create(world);
            case 29 -> EntityType.TADPOLE.create(world);
            case 30 -> EntityType.WOLF.create(world);
            case 31 -> EntityType.BEE.create(world);
            case 32 -> EntityType.DOLPHIN.create(world);
            case 33 -> EntityType.LLAMA.create(world);
            case 34 -> EntityType.PARROT.create(world);
            case 35 -> EntityType.GLOW_SQUID.create(world);
            case 36 -> EntityType.SQUID.create(world);
            case 37 -> EntityType.SNIFFER.create(world);
            case 38 -> EntityType.SNOW_GOLEM.create(world);
            default -> throw new IllegalStateException("Unexpected passive");
        };

        EffectUtils.updateSpawnPos(world, entity, 20, 5, true);
        if (world.getLightLevel(entity.getBlockPos()) < 8) return;
        world.spawnEntity(entity);
    }
}
