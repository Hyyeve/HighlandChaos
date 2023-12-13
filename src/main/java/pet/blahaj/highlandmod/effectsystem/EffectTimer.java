package pet.blahaj.highlandmod.effectsystem;

import java.util.Random;

public class EffectTimer {

    public int delayCounter;
    public int delayTime;
    public static final Random random = new Random();
    public final EffectSettings settings;

    public EffectTimer(EffectSettings settings) {
        this.settings = settings;
        this.delayCounter = random.nextInt(settings.minDelayTicks, settings.maxDelayTicks + 1);
        delayTime = delayCounter;
    }

    public static float random() {
        return random.nextFloat();
    }

    public static float random(float min, float max) {
        if(max <= min) return min;
        return random.nextFloat(min, max);
    }

    public static int random_int(int bound) {
        if(bound <= 0) return 0;
        return random.nextInt(bound);
    }

    public static int random_int(int min, int max) {
        return random.nextInt(min, max + 1);
    }

    public boolean tick_and_check() {
        if(!settings.enabled || !EffectManager.running) return false;
        delayCounter--;

        if(delayCounter <= 0 && random.nextFloat() < settings.probability) {
            delayCounter = random.nextInt(settings.minDelayTicks, settings.maxDelayTicks + 1);
            delayTime = delayCounter;
            return true;
        }
        return false;
    }
}
