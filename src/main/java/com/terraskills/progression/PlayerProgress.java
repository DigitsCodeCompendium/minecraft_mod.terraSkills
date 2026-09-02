package com.terraskills.progression;

import com.terraskills.TerraSkills;
import com.terraskills.config.TerraSkillsConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

public final class PlayerProgress {
    private static final String ROOT_KEY = TerraSkills.MOD_ID + ":progress";
    private static final String SELECTED_TREE = "selectedTree";
    private static final String ACCUMULATED_POINTS = "accumulatedPointsByTree";
    private static final String LAST_REAL_TIME = "lastRealTimeMillis";
    private static final String STATS = "stats";

    private PlayerProgress() {}

    private static CompoundTag data(ServerPlayer player) {
        CompoundTag persistent = player.getPersistentData();
        if (!persistent.contains(ROOT_KEY)) persistent.put(ROOT_KEY, new CompoundTag());
        return persistent.getCompound(ROOT_KEY);
    }

    public static Optional<ResourceLocation> selectedTree(ServerPlayer player) {
        String value = data(player).getString(SELECTED_TREE);
        return value.isBlank() ? Optional.empty() : Optional.ofNullable(ResourceLocation.tryParse(value));
    }

    public static void selectTree(ServerPlayer player, ResourceLocation tree) {
        data(player).putString(SELECTED_TREE, tree.toString());
    }

    public static int getStat(ServerPlayer player, RpgStat stat) {
        return data(player).getCompound(STATS).getInt(stat.id());
    }

    public static void setStat(ServerPlayer player, RpgStat stat, int value) {
        CompoundTag root = data(player);
        CompoundTag stats = root.getCompound(STATS);
        stats.putInt(stat.id(), Math.clamp(value, 0, TerraSkillsConfig.MAX_STAT_VALUE.get()));
        root.put(STATS, stats);
    }

    public static double accumulatedPoints(ServerPlayer player, ResourceLocation tree) {
        return data(player).getCompound(ACCUMULATED_POINTS).getDouble(tree.toString());
    }

    public static void setAccumulatedPoints(ServerPlayer player, ResourceLocation tree, double value) {
        CompoundTag root = data(player);
        CompoundTag points = root.getCompound(ACCUMULATED_POINTS);
        points.putDouble(tree.toString(), Math.max(0, value));
        root.put(ACCUMULATED_POINTS, points);
    }

    public static long lastRealTimeMillis(ServerPlayer player) {
        return data(player).getLong(LAST_REAL_TIME);
    }

    public static void setLastRealTimeMillis(ServerPlayer player, long time) {
        data(player).putLong(LAST_REAL_TIME, time);
    }

    public static void copy(ServerPlayer oldPlayer, ServerPlayer newPlayer) {
        CompoundTag oldData = oldPlayer.getPersistentData().getCompound(ROOT_KEY);
        newPlayer.getPersistentData().put(ROOT_KEY, oldData.copy());
    }
}
