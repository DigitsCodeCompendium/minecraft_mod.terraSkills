package com.terraskills.client;

import com.terraskills.config.TerraSkillsClientConfig;
import com.terraskills.network.SkillProgressPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.Locale;

public final class SkillPointHud {
    private static final int WIDTH = 194;
    private static final int HEIGHT = 39;
    private static final int CONTENT_X = 42;
    private static final int[] NUTRIENT_COLORS = {0xFFE2B84B, 0xFFE45B78, 0xFF63B85E, 0xFFD8683D, 0xFFF0EEE3};
    private static SkillProgressPayload state = new SkillProgressPayload("", 0, 0, 1, false, 0, 0, 0, 0, 0, 0, 0);

    private SkillPointHud() {}
    public static void accept(SkillProgressPayload payload) { state = payload; }

    public static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!TerraSkillsClientConfig.SHOW_HUD.get() || minecraft.options.hideGui || minecraft.player == null) return;

        double scale = TerraSkillsClientConfig.HUD_SCALE.get();
        double screenX = graphics.guiWidth() * TerraSkillsClientConfig.HORIZONTAL_POSITION.get() / 100.0;
        double screenY = graphics.guiHeight() * TerraSkillsClientConfig.VERTICAL_POSITION.get() / 100.0;
        TerraSkillsClientConfig.Anchor anchor = TerraSkillsClientConfig.HUD_ANCHOR.get();
        graphics.pose().pushPose();
        graphics.pose().translate((float) screenX, (float) screenY, 0);
        graphics.pose().scale((float) scale, (float) scale, 1);
        graphics.pose().translate((float) (-WIDTH * anchor.horizontal()), (float) (-HEIGHT * anchor.vertical()), 0);

        renderVanillaPanel(graphics);
        renderNutritionWheel(graphics);
        boolean hasTree = !state.tree().isEmpty();
        String treeName = hasTree ? readableTreeName(state.tree()) : Component.translatable("terraskills.hud.no_tree").getString();
        graphics.drawString(minecraft.font, Component.literal(treeName).withStyle(ChatFormatting.GOLD), CONTENT_X, 5, 0xFFFFFF, true);
        double progress = Math.clamp(state.progress(), 0, 1);
        int barX = CONTENT_X, barY = 17, barWidth = WIDTH - CONTENT_X - 6;
        renderExperienceBar(graphics, barX, barY, barWidth, progress);
        String percentage = String.format(Locale.ROOT, "%.1f%%", progress * 100);
        graphics.drawString(minecraft.font, percentage, WIDTH - 6 - minecraft.font.width(percentage), 5, 0xFFFFFFFF, true);
        String remaining = hasTree ? remainingTime(progress) : Component.translatable("terraskills.hud.select_tree").getString();
        graphics.drawString(minecraft.font, Component.translatable("terraskills.hud.next_point"), CONTENT_X, 28, 0xFFA0A0A0, true);
        graphics.drawString(minecraft.font, remaining, WIDTH - 6 - minecraft.font.width(remaining), 28, 0xFFFFFFFF, true);
        graphics.pose().popPose();
    }

    private static void renderVanillaPanel(GuiGraphics graphics) {
        // Vanilla widget-style black outline, raised top/left edges, and recessed interior.
        graphics.fill(0, 0, WIDTH, HEIGHT, 0xFF000000);
        graphics.fill(1, 1, WIDTH - 1, HEIGHT - 1, 0xFF373737);
        graphics.fill(1, 1, WIDTH - 2, 2, 0xFFFFFFFF);
        graphics.fill(1, 1, 2, HEIGHT - 2, 0xFFFFFFFF);
        graphics.fill(2, HEIGHT - 2, WIDTH - 1, HEIGHT - 1, 0xFF555555);
        graphics.fill(WIDTH - 2, 2, WIDTH - 1, HEIGHT - 1, 0xFF555555);

        // Recessed square around the nutrition dial, like an inventory slot.
        graphics.fill(4, 2, 40, 37, 0xFF111111);
        graphics.fill(5, 3, 39, 36, 0xFF8B8B8B);
        graphics.fill(6, 4, 38, 35, 0xFF2B2B2B);
    }

    private static void renderExperienceBar(GuiGraphics graphics, int x, int y, int width, double progress) {
        graphics.fill(x, y, x + width, y + 8, 0xFF000000);
        graphics.fill(x + 1, y + 1, x + width - 1, y + 7, 0xFF202020);
        int filled = (int) Math.round((width - 2) * progress);
        if (filled > 0) {
            graphics.fill(x + 1, y + 1, x + 1 + filled, y + 3, 0xFF80FF20);
            graphics.fill(x + 1, y + 3, x + 1 + filled, y + 6, 0xFF55AA00);
            graphics.fill(x + 1, y + 6, x + 1 + filled, y + 7, 0xFF285500);
        }
        // Pixel divisions make the bar resemble the vanilla experience meter.
        for (int segment = 1; segment < 10; segment++) {
            int segmentX = x + 1 + (width - 2) * segment / 10;
            graphics.fill(segmentX, y + 1, segmentX + 1, y + 7, 0x80000000);
        }
    }

    private static void renderNutritionWheel(GuiGraphics graphics) {
        int centerX = 22;
        int centerY = 19;
        int maximumRadius = 14;
        drawCircle(graphics, centerX, centerY, maximumRadius + 1, 0xFF000000);
        drawCircle(graphics, centerX, centerY, maximumRadius, 0xFF181818);
        if (state.maximumNutrition() <= 0 || state.nutritionBeforeBalance() <= 0) return;

        // Scaling radius by the square root makes the visible wheel area proportional to the unbalanced base amount.
        double amountRatio = Math.clamp(state.nutritionBeforeBalance() / state.maximumNutrition(), 0, 1);
        int radius = Math.max(1, (int) Math.round(maximumRadius * Math.sqrt(amountRatio)));
        double[] nutrients = {state.grain(), state.fruit(), state.vegetables(), state.protein(), state.dairy()};
        double total = 0;
        for (double nutrient : nutrients) total += Math.max(0, nutrient);
        if (total <= 0) return;

        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                if (x * x + y * y > radius * radius) continue;
                // Start at twelve o'clock and proceed clockwise through grain, fruit, vegetables, protein, and dairy.
                double angle = (Math.atan2(x, -y) + Math.PI * 2) % (Math.PI * 2);
                double share = angle / (Math.PI * 2) * total;
                double boundary = 0;
                int color = NUTRIENT_COLORS[NUTRIENT_COLORS.length - 1];
                for (int index = 0; index < nutrients.length; index++) {
                    boundary += Math.max(0, nutrients[index]);
                    if (share <= boundary) { color = NUTRIENT_COLORS[index]; break; }
                }
                graphics.fill(centerX + x, centerY + y, centerX + x + 1, centerY + y + 1, color);
            }
        }
    }

    private static void drawCircle(GuiGraphics graphics, int centerX, int centerY, int radius, int color) {
        for (int y = -radius; y <= radius; y++) {
            int halfWidth = (int) Math.floor(Math.sqrt(radius * radius - y * y));
            graphics.fill(centerX - halfWidth, centerY + y, centerX + halfWidth + 1, centerY + y + 1, color);
        }
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

    private static String readableTreeName(String id) {
        int separator = id.indexOf(':');
        String[] words = (separator >= 0 ? id.substring(separator + 1) : id).replace('/', '_').split("_");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!result.isEmpty()) result.append(' ');
            if (!word.isEmpty()) result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return result.toString();
    }
}
