package pet.blahaj.highlandmod.effectsystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import java.io.*;
import java.util.HashMap;
import java.util.NoSuchElementException;

public class SaveUtil {
    public static void save_preset(String name, HashMap<String, EffectSettings> settingsList) throws IOException {
        File file = new File("highland-presets/" + name + ".preset");
        createFile(file);
        FileOutputStream of = new FileOutputStream(file);
        ObjectOutputStream os = new ObjectOutputStream(of);
        settingsList.forEach((key, value) -> {
            try {
                os.writeObject(new SettingContainer(key, value));
            } catch (IOException e) {
            }
        });
        os.close();
        of.close();
    }

    public static void load_preset(String name) throws IOException, ClassNotFoundException {
        FileInputStream fi = new FileInputStream("highland-presets/" + name + ".preset");
        ObjectInputStream os = new ObjectInputStream(fi);

        while (true) {
            try {
                SettingContainer setting = (SettingContainer) os.readObject();
                EffectManager.set_effect_parameters(setting.name, setting.settings.probability, (float) setting.settings.minDelayTicks / 20, (float) setting.settings.maxDelayTicks / 20);
                EffectManager.set_effect_enabled(setting.name, setting.settings.enabled);
            }
            catch (ClassNotFoundException | IOException e) {
                break;
            }
        }
    }

    record SettingContainer(String name, EffectSettings settings) implements Serializable {}

    public static boolean createFile(File file) {
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) return false;
            if (file.createNewFile()) return true;
        } catch (Exception ignored) {
        }

        return false;
    }

    public static void copy_out_preset(String builtin_preset) throws IOException {
        InputStream is = getResourceStream(new Identifier("highland",builtin_preset + ".preset"));
        if(is == null) return;
        File file = new File("highland-presets/" + builtin_preset + ".preset");
        createFile(file);
        FileOutputStream of = new FileOutputStream(file);
        of.write(is.readAllBytes());
        of.close();
        is.close();
    }

    public static InputStream getResourceStream(Identifier resource) {
        try {
            return MinecraftClient.getInstance().getResourceManager().getResource(resource).orElseThrow().getInputStream();
        } catch (IOException | NoSuchElementException e) {
            return null;
        }
    }

}
