package pet.blahaj.highlandmod.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.MagmaCubeEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow
    @Nullable
    public ClientWorld world;
    @Unique
    Random random = new Random();

    @Unique
    int monsterDelay = 0;

    @Unique
    int passiveDelay = 0;

    @Unique
    int itemDelay = 0;

    @Unique
    int fallingBlockDelay = 0;

    @Unique
    int swappingBlockDelay = 0;
    @Unique
    int itemShuffleDelay;

    @Inject(method = "tick", at = @At("HEAD"))
    public void preTick(CallbackInfo ci) {

        if (MinecraftClient.getInstance().world == null || MinecraftClient.getInstance().isPaused()) return;

        ServerWorld world = MinecraftClient.getInstance().getServer().getWorld(World.OVERWORLD);
        handleHostileSpawns(world);
        handlePassiveSpawns(world);
        handleItemSpawns(world);
        handleFallingBlocks(world);
        handleSwappingBlocks(world);
        handleItemShuffle(world);

        world = MinecraftClient.getInstance().getServer().getWorld(World.NETHER);
        handleHostileSpawns(world);
        handlePassiveSpawns(world);
        handleItemSpawns(world);
        handleFallingBlocks(world);
        handleSwappingBlocks(world);
        handleItemShuffle(world);

        world = MinecraftClient.getInstance().getServer().getWorld(World.END);
        handleHostileSpawns(world);
        handlePassiveSpawns(world);
        handleItemSpawns(world);
        handleFallingBlocks(world);
        handleSwappingBlocks(world);
        handleItemShuffle(world);
    }

    @Unique
    private void handleHostileSpawns(ServerWorld world) {
        if (monsterDelay > 0) monsterDelay--;
        else {
            monsterDelay = random.nextInt(80);
            spawnMonster(world);
        }
    }

    @Unique
    private void handlePassiveSpawns(ServerWorld world) {
        if (passiveDelay > 0) passiveDelay--;
        else {
            passiveDelay = random.nextInt(90);
            spawnPassive(world);
        }
    }

    @Unique
    private void handleItemSpawns(ServerWorld world) {
        if (itemDelay > 0) itemDelay--;
        else {
            itemDelay = random.nextInt(300);
            spawnItem(world);
        }
    }

    @Unique
    private void handleFallingBlocks(ServerWorld world) {
        if (fallingBlockDelay > 0) fallingBlockDelay--;
        else {
            fallingBlockDelay = random.nextInt(50);
            spawnBlock(world);
        }
    }

    @Unique
    private void handleSwappingBlocks(ServerWorld world) {
        if (swappingBlockDelay > 0) swappingBlockDelay--;
        else {
            swappingBlockDelay = random.nextInt(30);
            swapBlock(world);
        }
    }

    @Unique
    private void handleItemShuffle(ServerWorld world) {
        if (itemShuffleDelay > 0) itemShuffleDelay--;
        else {
            itemShuffleDelay = random.nextInt(300);
            shuffleItems(world);
        }
    }

    @Unique
    private void shuffleItems(ServerWorld world) {
        for (PlayerEntity player : world.getPlayers()) {
            PlayerInventory inventory = player.getInventory();
            for (int i = 0; i < 3; i++) {
                int slota = random.nextInt(inventory.size());
                ItemStack stack = inventory.getStack(slota);
                if (stack.isEmpty()) continue;
                int slot = random.nextInt(inventory.size());
                inventory.setStack(slota, inventory.getStack(slot));
                inventory.setStack(slot, stack);
            }

            for (int i = 0; i < 5; i++) {
                int slot = random.nextInt(inventory.size());
                ItemStack stack = inventory.getStack(slot);
                if (stack.isEmpty()) continue;
                stack = stack.copyWithCount(stack.getCount() + random.nextInt(5));
                inventory.setStack(slot, stack);
            }

            for (int i = 0; i < 5; i++) {
                int slot = random.nextInt(inventory.size());
                ItemStack stack = inventory.getStack(slot);
                if (stack.isEmpty()) continue;
                stack = stack.copyWithCount(stack.getCount() - random.nextInt(5));
                inventory.setStack(slot, stack);
            }
        }
    }

    @Unique
    private BlockPos randomPos(Vec3d origin, int horizontal, int vertical) {
        int x = (int) (origin.getX() + random.nextInt(horizontal * 2) - horizontal);
        int y = (int) (origin.getY() + random.nextInt(vertical * 2) - vertical);
        int z = (int) (origin.getZ() + random.nextInt(horizontal * 2) - horizontal);
        return new BlockPos(x, y, z);
    }

    @Unique
    private void updateSpawnPos(ServerWorld world, Entity entity, int horizontalDist, int verticalDist, boolean forceVisible) {
        int players = world.getPlayers().size();
        if(players == 0) return;
        PlayerEntity player = world.getPlayers().get(random.nextInt(players));
        BlockPos pos = randomPos(player.getPos(), horizontalDist, verticalDist);
        entity.updatePosition(pos.getX(), pos.getY(), pos.getZ());
        int attempts = 30;
        while (!world.isSpaceEmpty(entity) && (!forceVisible || !player.canSee(entity)) && attempts-- > 0) {
            player = world.getPlayers().get(random.nextInt(players));
            pos = randomPos(player.getPos(), horizontalDist, verticalDist);
            entity.updatePosition(pos.getX(), pos.getY(), pos.getZ());
        }
    }

    @Unique
    private void spawnMonster(ServerWorld world) {

        int difficulty = (int) (Math.pow(random.nextDouble(), 3) * 4);

        Entity entity = switch (difficulty) {
            case 0:
                yield switch (random.nextInt(12)) {
                    case 0 -> {
                        ZombieEntity zomb = random.nextInt(10) == 0 ? EntityType.ZOMBIE_VILLAGER.create(world) : EntityType.ZOMBIE.create(world);
                        zomb.setBaby(random.nextInt(100) == 0);
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
                        slime.setSize(random.nextInt(3) + 1, true);
                        if (random.nextInt(100) == 0) slime.setSize(5, true);
                        yield slime;
                    }
                    case 8 -> EntityType.SPIDER.create(world);
                    case 9 -> EntityType.ZOMBIFIED_PIGLIN.create(world);
                    case 10 -> EntityType.PIGLIN.create(world);
                    case 11 -> EntityType.ENDERMAN.create(world);
                    default -> throw new IllegalStateException("Unexpected easy monster");
                };
            case 1:
                yield switch (random.nextInt(7)) {
                    case 0 -> EntityType.WITHER_SKELETON.create(world);
                    case 1 -> EntityType.SKELETON.create(world);
                    case 2 -> {
                        MagmaCubeEntity slime = EntityType.MAGMA_CUBE.create(world);
                        slime.setSize(random.nextInt(3) + 1, true);
                        if (random.nextInt(100) == 0) slime.setSize(5, true);
                        yield slime;
                    }
                    case 3 -> EntityType.PILLAGER.create(world);
                    case 4 -> EntityType.STRAY.create(world);
                    case 5 -> EntityType.GHAST.create(world);
                    case 6 -> EntityType.GUARDIAN.create(world);
                    default -> throw new IllegalStateException("Unexpected medium monster");
                };
            case 2:
                yield switch (random.nextInt(6)) {
                    case 0 -> EntityType.SHULKER.create(world);
                    case 1 -> EntityType.WITCH.create(world);
                    case 2 -> EntityType.VEX.create(world);
                    case 3 -> EntityType.BLAZE.create(world);
                    case 4 -> EntityType.CAVE_SPIDER.create(world);
                    case 5 -> EntityType.HOGLIN.create(world);
                    default -> throw new IllegalStateException("Unexpected tricky monster");
                };
            case 3:
                yield switch (random.nextInt(6)) {
                    case 0 -> EntityType.ELDER_GUARDIAN.create(world);
                    case 1 -> EntityType.EVOKER.create(world);
                    case 2 -> EntityType.PIGLIN_BRUTE.create(world);
                    case 3 -> EntityType.RAVAGER.create(world);
                    case 4 -> EntityType.VINDICATOR.create(world);
                    case 5 -> EntityType.ZOGLIN.create(world);
                    default -> throw new IllegalStateException("Unexpected hard monster");
                };
            case 4:
                yield switch (random.nextInt(3)) {
                    case 0 -> EntityType.ENDER_DRAGON.create(world);
                    case 1 -> EntityType.WARDEN.create(world);
                    case 2 -> EntityType.WITHER.create(world);
                    default -> throw new IllegalStateException("Unexpected boss");
                };
            default:
                throw new IllegalStateException("Unexpected difficulty: " + difficulty);
        };

        updateSpawnPos(world, entity, 20, 5, true);
        if (world.getLightLevel(entity.getBlockPos()) > 3) return;
        world.spawnEntity(entity);

    }

    @Unique
    private void spawnPassive(ServerWorld world) {
        Entity entity = switch (random.nextInt(39)) {
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

        updateSpawnPos(world, entity, 20, 5, true);
        if (world.getLightLevel(entity.getBlockPos()) < 3) return;
        world.spawnEntity(entity);
    }

    @Unique
    private void spawnItem(ServerWorld world) {
        ItemEntity item = EntityType.ITEM.create(world);
        int maxItem = Registries.ITEM.size();
        item.setStack(new ItemStack(Registries.ITEM.get(random.nextInt(maxItem)), (int) (Math.pow(random.nextDouble(), 5) * 63) + 1));
        updateSpawnPos(world, item, 5, 1, true);
        world.spawnEntity(item);
    }

    @Unique
    private void spawnBlock(ServerWorld world) {
        int players = world.getPlayers().size();
        if(players == 0) return;
        PlayerEntity player = world.getPlayers().get(random.nextInt(players));
        BlockPos pos = randomPos(player.getPos(), 10, 10);
        int attempts = 30;
        while (world.getBlockState(pos).isAir() || !world.getBlockState(pos.down()).isAir() && attempts-- > 0)
            pos = randomPos(player.getPos(), 10, 10);
        FallingBlockEntity entity = FallingBlockEntity.spawnFromBlock(world, pos, world.getBlockState(pos));

        if (random.nextInt(3) == 0) {
            FallingBlockEntity.spawnFromBlock(world, pos.add(0, 0, 1), world.getBlockState(pos.add(0, 0, 1)));
            FallingBlockEntity.spawnFromBlock(world, pos.add(1, 0, 0), world.getBlockState(pos.add(1, 0, 0)));
            FallingBlockEntity.spawnFromBlock(world, pos.add(1, 0, 1), world.getBlockState(pos.add(1, 0, 1)));
            FallingBlockEntity.spawnFromBlock(world, pos.add(0, 1, 1), world.getBlockState(pos.add(0, 1, 1)));
            FallingBlockEntity.spawnFromBlock(world, pos.add(1, 1, 0), world.getBlockState(pos.add(1, 1, 0)));
            FallingBlockEntity.spawnFromBlock(world, pos.add(1, 1, 1), world.getBlockState(pos.add(1, 1, 1)));
        }

        entity.setHurtEntities(2.0f, 40);
    }

    @Unique
    private void swapBlock(ServerWorld world) {
        int players = world.getPlayers().size();
        if(players == 0) return;
        PlayerEntity player = world.getPlayers().get(random.nextInt(players));
        BlockPos pos = randomPos(player.getPos(), 10, 10);
        int attempts = 30;
        while (world.getBlockState(pos).isAir() || !world.isSkyVisible(pos) && attempts-- > 0)
            pos = randomPos(player.getPos(), 10, 10);
        BlockPos pos2 = randomPos(pos.toCenterPos(), 5, 5);
        if (pos2.equals(pos)) return;
        BlockState state = world.getBlockState(pos);
        BlockState state2 = world.getBlockState(pos2);
        world.setBlockState(pos, state2);
        world.setBlockState(pos2, state);
    }
}
