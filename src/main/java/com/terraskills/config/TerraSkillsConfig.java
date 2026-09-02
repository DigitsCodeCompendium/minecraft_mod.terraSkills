package com.terraskills.config;

import com.terraskills.progression.RpgStat;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public final class TerraSkillsConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue BASE_POINTS_PER_DAY;
    public static final ModConfigSpec.DoubleValue REAL_MINUTES_PER_DAY;
    public static final ModConfigSpec.BooleanValue GENERATION_ENABLED;
    public static final ModConfigSpec.DoubleValue GRAIN_POINTS;
    public static final ModConfigSpec.DoubleValue FRUIT_POINTS;
    public static final ModConfigSpec.DoubleValue VEGETABLE_POINTS;
    public static final ModConfigSpec.DoubleValue PROTEIN_POINTS;
    public static final ModConfigSpec.DoubleValue DAIRY_POINTS;
    public static final ModConfigSpec.DoubleValue NUTRITION_EXPONENT;
    public static final ModConfigSpec.DoubleValue MINIMUM_BALANCE_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue BALANCE_EXPONENT;
    public static final ModConfigSpec.DoubleValue PRIMARY_BONUS_PER_STAT;
    public static final ModConfigSpec.DoubleValue SECONDARY_BONUS_PER_STAT;
    public static final ModConfigSpec.DoubleValue MAXIMUM_STAT_MULTIPLIER;
    public static final ModConfigSpec.IntValue MAX_STAT_VALUE;
    public static final ModConfigSpec.BooleanValue ACCRUE_WHILE_OFFLINE;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SKILL_TREES;
    public static final ModConfigSpec SPEC;

    static {
        BUILDER.push("generation");
        BASE_POINTS_PER_DAY = decimal("baseSkillPointsPerDay", 1.0, 0, 10000,
                "Points generated per configured real-time skill day before nutrition and stats.");
        REAL_MINUTES_PER_DAY = decimal("realMinutesPerSkillDay", 1440.0, 0.0167, 525600,
                "Number of real-world minutes in one skill day. 1440 is a real 24-hour day; lower values are useful for testing.");
        GENERATION_ENABLED = BUILDER.comment("Master startup switch for passive generation. Admins can also pause/resume the live server with commands.")
                .define("enabled", true);
        ACCRUE_WHILE_OFFLINE = BUILDER.comment("If true, calendar time while logged out is awarded on login using the player's current nutrition.")
                .define("accrueWhileOffline", false);
        BUILDER.pop();

        BUILDER.push("nutrition");
        GRAIN_POINTS = nutrient("grainPointsPerDayAtFull", 0.2);
        FRUIT_POINTS = nutrient("fruitPointsPerDayAtFull", 0.2);
        VEGETABLE_POINTS = nutrient("vegetablePointsPerDayAtFull", 0.2);
        PROTEIN_POINTS = nutrient("proteinPointsPerDayAtFull", 0.2);
        DAIRY_POINTS = nutrient("dairyPointsPerDayAtFull", 0.2);
        NUTRITION_EXPONENT = decimal("fullnessExponent", 1.0, 0.05, 10,
                "Curve applied to each normalized nutrient. Above 1 rewards high fullness more strongly; below 1 is more forgiving.");
        MINIMUM_BALANCE_MULTIPLIER = decimal("minimumBalanceMultiplier", 0.25, 0, 1,
                "Lowest possible nutrition balance multiplier. Perfect balance always scales to 1.");
        BALANCE_EXPONENT = decimal("balanceExponent", 1.0, 0.05, 10,
                "Curve applied to the average closeness of every pair of nutrients when calculating balance.");
        BUILDER.pop();

        BUILDER.push("rpgStats");
        PRIMARY_BONUS_PER_STAT = decimal("primaryBonusPerPoint", 0.05, 0, 100,
                "Additive generation multiplier per point in a tree's primary stat. 0.05 means +5%.");
        SECONDARY_BONUS_PER_STAT = decimal("secondaryBonusPerPoint", 0.025, 0, 100,
                "Additive generation multiplier per point in a tree's secondary stat.");
        MAXIMUM_STAT_MULTIPLIER = decimal("maximumMultiplier", 10.0, 1, 10000,
                "Hard cap on the combined primary/secondary stat multiplier.");
        MAX_STAT_VALUE = BUILDER.comment("Maximum value accepted for any RPG stat.")
                .defineInRange("maximumStatValue", 100, 0, 1_000_000);
        BUILDER.pop();

        BUILDER.push("skillTrees");
        SKILL_TREES = BUILDER.comment(
                        "Available Pufferfish skill trees and their preferred stats.",
                        "Format: category_id,primary_stat,secondary_stat",
                        "Stats: might, finesse, endurance, intelligence, instinct")
                .defineListAllowEmpty("trees", List.of(
                        "terraskills:warrior,might,endurance",
                        "terraskills:scholar,intelligence,instinct",
                        "terraskills:miner,endurance,instinct"
                ), () -> "", TerraSkillsConfig::validTree);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private TerraSkillsConfig() {}

    private static ModConfigSpec.DoubleValue nutrient(String key, double value) {
        return decimal(key, value, 0, 10000, "Points per day contributed when this nutrient is 100% full.");
    }

    private static ModConfigSpec.DoubleValue decimal(String key, double value, double min, double max, String comment) {
        return BUILDER.comment(comment).defineInRange(key, value, min, max);
    }

    private static boolean validTree(Object value) {
        if (!(value instanceof String text)) return false;
        String[] parts = text.split(",", -1);
        return parts.length == 3 && ResourceLocation.tryParse(parts[0].trim()) != null
                && RpgStat.parse(parts[1]).isPresent() && RpgStat.parse(parts[2]).isPresent();
    }
}
