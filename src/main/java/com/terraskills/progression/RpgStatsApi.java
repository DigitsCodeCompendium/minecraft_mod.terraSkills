package com.terraskills.progression;

import net.minecraft.server.level.ServerPlayer;

/**
 * Public integration surface for mods that grant permanent base RPG stats.
 * Temporary or origin-owned bonuses should use the corresponding synchronized
 * Minecraft attribute from {@link TerraSkillsAttributes} so removal is automatic.
 */
public final class RpgStatsApi {
    private RpgStatsApi() {}

    public static int get(ServerPlayer player, RpgStat stat) {
        return PlayerProgress.getStat(player, stat);
    }

    public static double getAttributeBonus(ServerPlayer player, RpgStat stat) {
        return player.getAttributeValue(TerraSkillsAttributes.get(stat));
    }

    public static void set(ServerPlayer player, RpgStat stat, int value) {
        PlayerProgress.setStat(player, stat, value);
    }

    public static void add(ServerPlayer player, RpgStat stat, int amount) {
        set(player, stat, get(player, stat) + amount);
    }
}
