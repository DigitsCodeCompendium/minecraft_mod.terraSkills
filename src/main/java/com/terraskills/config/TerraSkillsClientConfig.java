package com.terraskills.config;

import com.digitscodecompendium.terralib.client.gui.HudAnchor;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class TerraSkillsClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue SHOW_HUD;
    public static final ModConfigSpec.DoubleValue HUD_SCALE;
    public static final ModConfigSpec.DoubleValue HORIZONTAL_POSITION;
    public static final ModConfigSpec.DoubleValue VERTICAL_POSITION;
    public static final ModConfigSpec.EnumValue<HudAnchor> HUD_ANCHOR;
    public static final ModConfigSpec SPEC;

    static {
        BUILDER.push("skillPointHud");
        SHOW_HUD = BUILDER.comment("Show progress toward the next skill point on the HUD.")
                .translation("terraskills.configuration.hudEnabled").define("enabled", true);
        HUD_SCALE = BUILDER.comment("HUD scale. 1 is normal size.").defineInRange("scale", 1.0, 0.25, 4.0);
        HORIZONTAL_POSITION = BUILDER.comment("Horizontal position as a percentage of screen width.").defineInRange("horizontalPercent", 98.0, 0.0, 100.0);
        VERTICAL_POSITION = BUILDER.comment("Vertical position as a percentage of screen height.").defineInRange("verticalPercent", 98.0, 0.0, 100.0);
        HUD_ANCHOR = BUILDER.comment("The point on the HUD attached to the configured screen position.").defineEnum("anchor", HudAnchor.BOTTOM_RIGHT);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private TerraSkillsClientConfig() {}

}
