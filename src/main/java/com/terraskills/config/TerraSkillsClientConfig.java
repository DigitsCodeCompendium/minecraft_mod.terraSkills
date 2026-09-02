package com.terraskills.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class TerraSkillsClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec.BooleanValue SHOW_HUD;
    public static final ModConfigSpec.DoubleValue HUD_SCALE;
    public static final ModConfigSpec.DoubleValue HORIZONTAL_POSITION;
    public static final ModConfigSpec.DoubleValue VERTICAL_POSITION;
    public static final ModConfigSpec.EnumValue<Anchor> HUD_ANCHOR;
    public static final ModConfigSpec SPEC;

    static {
        BUILDER.push("skillPointHud");
        SHOW_HUD = BUILDER.comment("Show progress toward the next skill point on the HUD.")
                .translation("terraskills.configuration.hudEnabled").define("enabled", true);
        HUD_SCALE = BUILDER.comment("HUD scale. 1 is normal size.").defineInRange("scale", 1.0, 0.25, 4.0);
        HORIZONTAL_POSITION = BUILDER.comment("Horizontal position as a percentage of screen width.").defineInRange("horizontalPercent", 98.0, 0.0, 100.0);
        VERTICAL_POSITION = BUILDER.comment("Vertical position as a percentage of screen height.").defineInRange("verticalPercent", 98.0, 0.0, 100.0);
        HUD_ANCHOR = BUILDER.comment("The point on the HUD attached to the configured screen position.").defineEnum("anchor", Anchor.BOTTOM_RIGHT);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private TerraSkillsClientConfig() {}

    public enum Anchor {
        TOP_LEFT(0, 0), TOP(0.5, 0), TOP_RIGHT(1, 0),
        LEFT(0, 0.5), CENTER(0.5, 0.5), RIGHT(1, 0.5),
        BOTTOM_LEFT(0, 1), BOTTOM(0.5, 1), BOTTOM_RIGHT(1, 1);

        private final double horizontal;
        private final double vertical;
        Anchor(double horizontal, double vertical) { this.horizontal = horizontal; this.vertical = vertical; }
        public double horizontal() { return horizontal; }
        public double vertical() { return vertical; }
    }
}
