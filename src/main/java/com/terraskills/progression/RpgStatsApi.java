package com.terraskills.progression;

import net.minecraft.server.level.ServerPlayer;

/** Public integration surface for Origins and other mods that grant RPG stats. */
public final class RpgStatsApi {
    private RpgStatsApi() {}

    public static int get(ServerPlayer player, RpgStat stat) {
        return PlayerProgress.getStat(player, stat);
    }

    public static void set(ServerPlayer player, RpgStat stat, int value) {
        PlayerProgress.setStat(player, stat, value);
    }

    public static void add(ServerPlayer player, RpgStat stat, int amount) {
        set(player, stat, get(player, stat) + amount);
    }
}
