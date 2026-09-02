package com.terraskills.progression;

import com.terraskills.config.TerraSkillsConfig;

/** Live server control layered on top of the config's startup master switch. */
public final class ProgressionRuntime {
    private static volatile boolean paused;

    private ProgressionRuntime() {}

    public static boolean isPaused() {
        return paused;
    }

    public static boolean isGenerating() {
        return TerraSkillsConfig.GENERATION_ENABLED.get() && !paused;
    }

    public static void pause() {
        paused = true;
    }

    public static void resume() {
        paused = false;
    }
}
