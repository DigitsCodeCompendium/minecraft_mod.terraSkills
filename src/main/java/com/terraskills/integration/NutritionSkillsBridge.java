package com.terraskills.integration;

import net.dries007.tfc.common.player.IPlayerInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.puffish.skillsmod.api.SkillsAPI;

import java.util.OptionalInt;

/** Server-side boundary between TFC nutrition and Pufferfish's Skills. */
public final class NutritionSkillsBridge {
    /** A distinct source lets TerraSkills-awarded points be tracked independently. */
    public static final ResourceLocation NUTRITION_POINT_SOURCE =
            ResourceLocation.fromNamespaceAndPath("terraskills", "nutrition");

    private NutritionSkillsBridge() {
    }

    /** Returns TFC's normalized average nutrition value, in the range [0, 1]. */
    public static float getAverageNutrition(ServerPlayer player) {
        return IPlayerInfo.get(player).nutrition().getAverageNutrition();
    }

    /** Returns the unspent point count, or empty when the category does not exist. */
    public static OptionalInt getAvailablePoints(ServerPlayer player, ResourceLocation categoryId) {
        return SkillsAPI.getCategory(categoryId)
                .map(category -> OptionalInt.of(category.getPointsLeft(player)))
                .orElseGet(OptionalInt::empty);
    }

    /** Adds nutrition-attributed points. Returns false when the category does not exist. */
    public static boolean addNutritionPoints(ServerPlayer player, ResourceLocation categoryId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Point amount must be positive");
        }

        return SkillsAPI.getCategory(categoryId).map(category -> {
            category.addPoints(player, NUTRITION_POINT_SOURCE, amount);
            return true;
        }).orElse(false);
    }
}
