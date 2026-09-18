package com.terraskills.client;

import com.digitscodecompendium.terralib.client.gui.ProgressChartHud;
import com.digitscodecompendium.terralib.util.TerraFormats;
import com.terraskills.config.TerraSkillsClientConfig;
import com.terraskills.network.SkillProgressPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class SkillPointHud {
    private static final int[] NUTRIENT_COLORS = {0xFFE2B84B, 0xFFE45B78, 0xFF63B85E, 0xFFD8683D, 0xFFF0EEE3};
    private static SkillProgressPayload state = new SkillProgressPayload("", 0, 0, 1, false, 0, 0, 0, 0, 0, 0, 0);

    private SkillPointHud() {}
    public static void accept(SkillProgressPayload payload) { state = payload; }

    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        if (!TerraSkillsClientConfig.SKILL_POINT_HUD.enabled()) return;
        boolean hasTree = !state.tree().isEmpty();
        String treeName = hasTree ? TerraFormats.humanizeIdentifier(state.tree()) : Component.translatable("terraskills.hud.no_tree").getString();
        double progress = Math.clamp(state.progress(), 0, 1);
        String remaining = hasTree ? remainingTime(progress) : Component.translatable("terraskills.hud.select_tree").getString();
        ProgressChartHud.render(graphics, new ProgressChartHud.Content(
                        Component.literal(treeName).withStyle(ChatFormatting.GOLD), progress,
                        Component.translatable("terraskills.hud.next_point"), Component.literal(remaining),
                        state.nutritionBeforeBalance(), state.maximumNutrition(),
                        new double[]{state.grain(), state.fruit(), state.vegetables(), state.protein(), state.dairy()},
                        NUTRIENT_COLORS),
                TerraSkillsClientConfig.SKILL_POINT_HUD);
    }

    private static String remainingTime(double progress) {
        if (!state.generating()) return Component.translatable("terraskills.hud.paused").getString();
        if (state.pointsPerDay() <= 0) return Component.translatable("terraskills.hud.no_progress").getString();
        long seconds = Math.max(0, Math.round((1 - progress) / state.pointsPerDay() * state.millisecondsPerDay() / 1000.0));
        long days = seconds / 86_400, hours = seconds % 86_400 / 3_600, minutes = seconds % 3_600 / 60, secs = seconds % 60;
        if (days > 0) return String.format(Locale.ROOT, "%dd %02dh", days, hours);
        if (hours > 0) return String.format(Locale.ROOT, "%dh %02dm", hours, minutes);
        return String.format(Locale.ROOT, "%dm %02ds", minutes, secs);
    }

}
