package pet.blahaj.highlandmod.effectsystem.effects;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import pet.blahaj.highlandmod.effectsystem.EffectManager;
import pet.blahaj.highlandmod.effectsystem.EffectSettings;
import pet.blahaj.highlandmod.effectsystem.EffectTimer;
import pet.blahaj.highlandmod.effectsystem.HighlandEffect;

public class InventoryShuffler implements HighlandEffect {
    EffectTimer timer;

    @Override
    public EffectSettings load_settings() {
        return new EffectSettings("InventoryShuffler", 1.0f, 1f, 10f);
    }

    @Override
    public void initialize() {
        timer = EffectManager.new_timer("InventoryShuffler");

        ServerTickEvents.START_WORLD_TICK.register((world -> {
            if(!timer.tick_and_check()) return;

            for (PlayerEntity player : world.getPlayers()) {
                PlayerInventory inventory = player.getInventory();
                for (int i = 0; i < 3; i++) {
                    int slota = EffectTimer.random_int(inventory.size());
                    ItemStack stack = inventory.getStack(slota);
                    if (stack.isEmpty()) continue;
                    int slot = EffectTimer.random_int(inventory.size());
                    inventory.setStack(slota, inventory.getStack(slot));
                    inventory.setStack(slot, stack);
                }

                for (int i = 0; i < 5; i++) {
                    int slot = EffectTimer.random_int(inventory.size());
                    ItemStack stack = inventory.getStack(slot);
                    if (stack.isEmpty()) continue;
                    stack = stack.copyWithCount(stack.getCount() + EffectTimer.random_int(5));
                    if(stack.getCount() > stack.getMaxCount()) stack.setCount(stack.getMaxCount());
                    inventory.setStack(slot, stack);
                }

                for (int i = 0; i < 5; i++) {
                    int slot = EffectTimer.random_int(inventory.size());
                    ItemStack stack = inventory.getStack(slot);
                    if (stack.isEmpty()) continue;
                    stack = stack.copyWithCount(stack.getCount() - EffectTimer.random_int(5));
                    if(stack.getCount() > stack.getMaxCount()) stack.setCount(stack.getMaxCount());
                    if(stack.getCount() <= 0) stack.setCount(1);
                    inventory.setStack(slot, stack);
                }
            }
        }));
    }
}
