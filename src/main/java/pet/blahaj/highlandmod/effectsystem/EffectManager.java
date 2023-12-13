package pet.blahaj.highlandmod.effectsystem;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import pet.blahaj.highlandmod.effectsystem.effects.*;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Set;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class EffectManager {

    public static boolean running = false;
    private static HashMap<String, EffectSettings> currentEffectSettings = new HashMap<>();

    public static void initialize_effects() {
        add(new VelocityRandomizer());
        add(new HostileSpawner());
        add(new PassiveSpawner());
        add(new ItemSpawner());
        add(new EntityDespawner());
        add(new InventoryShuffler());
        add(new BlockSwapper());
        add(new FallingBlocks());
        add(new PotionEffects());
        add(new Passengers());
        register_commands();
    }

    public static void set_effect_enabled(String name, boolean enabled) {
        get(name).enabled = enabled;
    }

    public static boolean get_effect_enabled(String name) {
        return get(name).enabled;
    }

    public static void set_effect_parameters(String name, float probability, float minDelay, float maxDelay) {
        EffectSettings settings = currentEffectSettings.get(name);
            settings.probability = probability;
            settings.minDelayTicks = (int) (minDelay * 20);
            settings.maxDelayTicks = (int) (maxDelay * 20);
    }

    public static boolean toggle_effect(String name) {
        set_effect_enabled(name, !get_effect_enabled(name));
        return get_effect_enabled(name);
    }

    public static EffectSettings get(String name) {
        return currentEffectSettings.get(name);
    }

    public static EffectTimer new_timer(String name) {
        return new EffectTimer(get(name));
    }

    private static void add(HighlandEffect effect) {
        EffectSettings settings = effect.load_settings();
        currentEffectSettings.put(settings.name, settings);
        effect.initialize();
    }

    private static void register_commands() {
        Set<String> effectNames = currentEffectSettings.keySet();

        DecimalFormat floatStringFormat = new DecimalFormat("0.0");

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            final LiteralArgumentBuilder<ServerCommandSource> builder = literal("highlandPreset").requires(source -> source.hasPermissionLevel(2));

                builder.then(literal("save").then(argument("name", StringArgumentType.string()).executes((context -> {
                    String name = StringArgumentType.getString(context, "name");
                    try {
                        SaveUtil.save_preset(name, currentEffectSettings);
                        context.getSource().sendFeedback(() -> Text.of("Saved preset!"), false);
                        return 1;
                    } catch (IOException e) {
                        context.getSource().sendFeedback(() -> Text.of("Failed to save preset - file error :("), false);
                        return 0;
                    }
                }))));
                builder.then(literal("load").then(argument("name", StringArgumentType.string()).executes((context -> {
                    String name = StringArgumentType.getString(context, "name");
                    try {
                        SaveUtil.load_preset(name);
                        context.getSource().sendFeedback(() -> Text.of("Loaded preset!"), false);
                        return 1;
                    } catch (IOException | ClassNotFoundException e) {
                        context.getSource().sendFeedback(() -> Text.of("Failed to load preset - are you sure it exists?"), false);
                        return 0;
                    }
                }))));

            dispatcher.register(builder);
        }));

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            final LiteralArgumentBuilder<ServerCommandSource> builder = literal("highlandToggle").requires(source -> source.hasPermissionLevel(2));
            for(String s : effectNames) {
                builder.then(literal(s).executes((context) -> {
                    boolean enabled = toggle_effect(s);
                    context.getSource().sendFeedback(() -> Text.literal("Toggled §9" + s + "§7 (now " + (enabled ? "§2enabled" : "§4disabled") + "§7)"), true);
                    return 1;
                }));
            }
            dispatcher.register(builder);
        }));


        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            final LiteralArgumentBuilder<ServerCommandSource> builder = literal("highlandStart").requires(source -> source.hasPermissionLevel(2));
            builder.executes((context -> {
                EffectManager.running = true;
                context.getSource().sendFeedback(() -> Text.of("Highland effects enabled!"), true);
                return 1;
            }));
            dispatcher.register(builder);
        }));

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            final LiteralArgumentBuilder<ServerCommandSource> builder = literal("highlandStop").requires(source -> source.hasPermissionLevel(2));
            builder.executes((context -> {
                EffectManager.running = false;
                context.getSource().sendFeedback(() -> Text.of("Highland effects disabled."), true);
                return 1;
            }));
            dispatcher.register(builder);
        }));

        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            final LiteralArgumentBuilder<ServerCommandSource> builder = literal("highlandSettings").requires(source -> source.hasPermissionLevel(2));
            for(String s : effectNames) {
                builder.then(literal(s)
                        .then(argument("probability", FloatArgumentType.floatArg(0, 1))
                                .then(argument("min delay", FloatArgumentType.floatArg(0))
                                        .then(argument("max delay", FloatArgumentType.floatArg(0))
                                                .executes((context -> {
                                                    float probability = FloatArgumentType.getFloat(context, "probability");
                                                    float minDelay = FloatArgumentType.getFloat(context, "min delay");
                                                    float maxDelay = FloatArgumentType.getFloat(context, "max delay");
                                                    set_effect_parameters(s, probability, minDelay, maxDelay);

                                                    context.getSource().sendFeedback(() -> Text.literal("Set parameters for §9" + s + "§7 (new parameters: §6" + floatStringFormat.format(probability) + "§7 probability per tick, §6" + floatStringFormat.format(minDelay) + "§7 min delay, §6" + floatStringFormat.format(maxDelay) + "§7 max delay)"),true);

                                                    return 1;
                                                })))
                                )).executes((context) -> {
                            EffectSettings instance = get(s);
                            context.getSource().sendFeedback(() -> Text.literal("§9" + s + "§7: " + (instance.enabled ? "§2Enabled" : "§4Disabled") + ". §7Probability: §6" + floatStringFormat.format(instance.probability) + "§7, Min Delay: §6" + floatStringFormat.format(instance.minDelayTicks / 20) + "§7, Max Delay: §6" + floatStringFormat.format(instance.maxDelayTicks / 20)), true);
                            return 1;
                        }));
            }
            dispatcher.register(builder);
        }));
    }
}
