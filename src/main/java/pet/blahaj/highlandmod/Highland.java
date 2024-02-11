package pet.blahaj.highlandmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import pet.blahaj.highlandmod.effectsystem.EffectManager;
import pet.blahaj.highlandmod.effectsystem.SaveUtil;

import java.io.IOException;


public class Highland implements ModInitializer {
    @Override
    public void onInitialize() {
        EffectManager.initialize_effects();

        ServerLifecycleEvents.SERVER_STARTED.register((context) -> {
            try {
                SaveUtil.copy_out_preset("default");
                SaveUtil.load_preset("default");
            } catch (IOException | ClassNotFoundException ignored) {
            }
        });
    }


}
