package pet.blahaj.highlandmod.effectsystem.effects;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.NbtTextContent;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;
import pet.blahaj.highlandmod.effectsystem.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ItemSpawner implements HighlandEffect {

    private EffectTimer itemSpawnTimer;

    @Override
    public EffectSettings load_settings() {
        return new EffectSettings("ItemSpawner", 0.1f, 0.5f, 11f);
    }

    @Override
    public void initialize() {
        itemSpawnTimer = EffectManager.new_timer("ItemSpawner");
        ServerTickEvents.START_WORLD_TICK.register((world) -> {
            if(itemSpawnTimer.tick_and_check()) spawnItem(world);
        });
    }


    private void spawnItem(ServerWorld world) {
        ItemEntity item = EntityType.ITEM.create(world);
        ItemStack stack = new ItemStack(Registries.ITEM.getRandom(Random.createLocal()).get().value(), (int) (Math.pow(EffectTimer.random(), 5) * 63) + 1);
        if(stack.getCount() > stack.getMaxCount()) stack.setCount(stack.getMaxCount());
        if(stack.itemMatches(Items.POTION.getRegistryEntry()) || stack.itemMatches(Items.SPLASH_POTION.getRegistryEntry()) || stack.itemMatches(Items.LINGERING_POTION.getRegistryEntry())) setPotionEffect(stack);
        if(EffectTimer.random() < 0.5) setEnchantments(stack);
        item.setStack(stack);
        EffectUtils.updateSpawnPos(world, item, 5, 1, true);
        world.spawnEntity(item);
    }

    private void setEnchantments(ItemStack stack) {
        List<EnchantmentLevelEntry> effects = createEnchantments(stack);
        for(EnchantmentLevelEntry entry : effects) {
            stack.addEnchantment(entry.enchantment, entry.level);
        }
    }

    private List<EnchantmentLevelEntry> createEnchantments(ItemStack stack) {
        int count = EffectTimer.random_int(1, 3);
        List<EnchantmentLevelEntry>  effects = new ArrayList<>();

        List<Enchantment> availableEffects = new ArrayList<>(Registries.ENCHANTMENT.stream().filter(enchant -> enchant.isAcceptableItem(stack)).toList());
        if(availableEffects.isEmpty()) return effects;

        for(int i = 0; i < count; i++) {
            int level = EffectTimer.random_int(1, 9);
            Enchantment effect = availableEffects.get(EffectTimer.random_int(availableEffects.size()));
            availableEffects.remove(effect);
            effects.add(new EnchantmentLevelEntry(effect, level));
            if(availableEffects.isEmpty()) break;
        }

        return effects;
    }

    private void setPotionEffect(ItemStack stack) {
        StatusEffectInstance[] effects = createPotionEffects();
        stack.setCustomName(Text.of("§9Highland Potion"));
        PotionUtil.setCustomPotionEffects(stack, List.of(effects));
        NbtCompound nbtCompound = stack.getOrCreateSubNbt(ItemStack.DISPLAY_KEY);
        NbtList list = new NbtList();
        NbtString text = NbtString.of(Text.Serializer.toJson(Text.of("§5§oHolds random powers...")));
        NbtString textl2 = NbtString.of(Text.Serializer.toJson(Text.of("§l§6" + effects.length + " effects")));
        list.add(text);
        list.add(textl2);
        nbtCompound.put(ItemStack.LORE_KEY, list);
        stack.getOrCreateNbt().putInt("HideFlags", 32);
    }

    private StatusEffectInstance[] createPotionEffects() {
        int count = EffectTimer.random_int(1, 5);
        StatusEffectInstance[] effects = new StatusEffectInstance[count];

        for(int i = 0; i < count; i++) {
            int duration = EffectTimer.random_int(20, 1200);
            int level = EffectTimer.random_int(9);

            StatusEffectInstance instance = new StatusEffectInstance(Registries.STATUS_EFFECT.getRandom(Random.create()).get().value(), duration, level);
            effects[i] = instance;
        }

        return effects;
    }
}
