package pet.blahaj.highlandmod.effectsystem;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class EffectUtils {
    public static void updateSpawnPos(ServerWorld world, Entity entity, int horizontalDist, int verticalDist, boolean forceVisible) {
        int players = world.getPlayers().size();
        if(players == 0) return;
        PlayerEntity player = world.getPlayers().get(EffectTimer.random_int(players));
        BlockPos pos = randomPos(player.getPos(), horizontalDist, verticalDist);
        entity.updatePosition(pos.getX(), pos.getY(), pos.getZ());
        int attempts = 30;
        while (!world.isSpaceEmpty(entity) && (!forceVisible || !player.canSee(entity)) && attempts-- > 0) {
            player = world.getPlayers().get(EffectTimer.random_int(players));
            pos = randomPos(player.getPos(), horizontalDist, verticalDist);
            entity.updatePosition(pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public static BlockPos randomPos(Vec3d origin, int horizontal, int vertical) {
        int x = (int) (origin.getX() + EffectTimer.random_int(horizontal * 2) - horizontal);
        int y = (int) (origin.getY() + EffectTimer.random_int(vertical * 2) - vertical);
        int z = (int) (origin.getZ() + EffectTimer.random_int(horizontal * 2) - horizontal);
        return new BlockPos(x, y, z);
    }
}
