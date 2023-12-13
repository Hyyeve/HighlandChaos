package pet.blahaj.highlandmod.effectsystem.effects;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import pet.blahaj.highlandmod.effectsystem.*;

public class BlockSwapper implements HighlandEffect {
    EffectTimer timer;

    @Override
    public EffectSettings load_settings() {
        return new EffectSettings("BlockSwapper", 0.5f, 0f, 2f);
    }

    @Override
    public void initialize() {
        timer = EffectManager.new_timer("BlockSwapper");

        ServerTickEvents.START_WORLD_TICK.register((world -> {
            if(!timer.tick_and_check()) return;

            int players = world.getPlayers().size();
            if(players == 0) return;
            PlayerEntity player = world.getPlayers().get(EffectTimer.random_int(players));
            BlockPos pos = EffectUtils.randomPos(player.getPos(), 10, 10);
            int attempts = 30;
            while ((world.getBlockState(pos).isAir() || !world.getBlockState(pos.down()).isAir()) && attempts > 0) {
                pos = EffectUtils.randomPos(player.getPos(), 10, 10);
                attempts--;
            }
            BlockPos pos2 = EffectUtils.randomPos(pos.toCenterPos(), 5, 5);
            if (pos2.equals(pos)) return;
            BlockState state = world.getBlockState(pos);
            BlockState state2 = world.getBlockState(pos2);
            world.setBlockState(pos, state2);
            world.setBlockState(pos2, state);
        }));
    }
}
