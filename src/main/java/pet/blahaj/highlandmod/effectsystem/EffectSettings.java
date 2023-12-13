package pet.blahaj.highlandmod.effectsystem;

import java.io.Serializable;
import java.util.Objects;

public final class EffectSettings implements Serializable {
    public final String name;
    public float probability;
    public int minDelayTicks;
    public int maxDelayTicks;
    public boolean enabled;

    public EffectSettings(String name, float probability, int minDelayTicks, int maxDelayTicks) {
        this.name = name;
        this.probability = probability;
        this.minDelayTicks = minDelayTicks;
        this.maxDelayTicks = maxDelayTicks;
        this.enabled = true;
    }

    public EffectSettings(String name, float probability, float minDelaySecs, float maxDelaySecs) {
        this(name, probability, (int) (minDelaySecs * 20), (int) (maxDelaySecs * 20));
    }
}