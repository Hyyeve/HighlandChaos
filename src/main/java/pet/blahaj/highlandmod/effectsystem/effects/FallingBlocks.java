package pet.blahaj.highlandmod.effectsystem.effects;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import pet.blahaj.highlandmod.effectsystem.*;

public class FallingBlocks implements HighlandEffect {
    EffectTimer timer;

    @Override
    public EffectSettings load_settings() {
        return new EffectSettings("FallingBlocks", 0.2f, 0f, 3f);
    }

    @Override
    public void initialize() {
        timer = EffectManager.new_timer("FallingBlocks");

        ServerTickEvents.START_WORLD_TICK.register((world -> {
            if(!timer.tick_and_check()) return;

            int players = world.getPlayers().size();
            if(players == 0) return;
            PlayerEntity player = world.getPlayers().get(EffectTimer.random_int(players));
            BlockPos pos = EffectUtils.randomPos(player.getPos(), 10, 10);
            int attempts = 30;
            while ((world.getBlockState(pos).isAir() || !world.getBlockState(pos.down()).isAir()) && attempts > 0) {
                pos = EffectUtils.randomPos(player.getPos(), 10, 10);
                while(world.getBlockState(pos).isAir() && !world.isSkyVisible(pos)) pos = pos.up();
                attempts--;
            }

            FallingBlockEntity entity = FallingBlockEntity.spawnFromBlock(world, pos, world.getBlockState(pos));

            if (EffectTimer.random_int(3) == 0) {
                FallingBlockEntity.spawnFromBlock(world, pos.add(0, 0, 1), world.getBlockState(pos.add(0, 0, 1)));
                FallingBlockEntity.spawnFromBlock(world, pos.add(1, 0, 0), world.getBlockState(pos.add(1, 0, 0)));
                FallingBlockEntity.spawnFromBlock(world, pos.add(1, 0, 1), world.getBlockState(pos.add(1, 0, 1)));
                FallingBlockEntity.spawnFromBlock(world, pos.add(0, 1, 1), world.getBlockState(pos.add(0, 1, 1)));
                FallingBlockEntity.spawnFromBlock(world, pos.add(1, 1, 0), world.getBlockState(pos.add(1, 1, 0)));
                FallingBlockEntity.spawnFromBlock(world, pos.add(1, 1, 1), world.getBlockState(pos.add(1, 1, 1)));
            }

            entity.setHurtEntities(0.1f, 5);
        }));
    }
}
