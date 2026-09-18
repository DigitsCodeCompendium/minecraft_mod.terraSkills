package com.terraskills.config;

import com.digitscodecompendium.terralib.client.gui.HudPanelConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class TerraSkillsClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final HudPanelConfig SKILL_POINT_HUD;
    public static final ModConfigSpec SPEC;

    static {
        SKILL_POINT_HUD = HudPanelConfig.define(BUILDER, "skillPointHud", "terraskills.configuration.skillPointHud");
        SPEC = BUILDER.build();
    }

    private TerraSkillsClientConfig() {}

}
