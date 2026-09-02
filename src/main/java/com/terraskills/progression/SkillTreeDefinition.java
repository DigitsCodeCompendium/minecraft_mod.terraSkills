package com.terraskills.progression;

import com.terraskills.config.TerraSkillsConfig;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Optional;

public record SkillTreeDefinition(ResourceLocation id, RpgStat primary, RpgStat secondary) {
    public static List<SkillTreeDefinition> configuredTrees() {
        return TerraSkillsConfig.SKILL_TREES.get().stream().map(String::valueOf)
                .map(SkillTreeDefinition::parse).flatMap(Optional::stream).toList();
    }

    public static Optional<SkillTreeDefinition> find(ResourceLocation id) {
        return configuredTrees().stream().filter(tree -> tree.id.equals(id)).findFirst();
    }

    private static Optional<SkillTreeDefinition> parse(String text) {
        String[] parts = text.split(",", -1);
        if (parts.length != 3) return Optional.empty();
        ResourceLocation id = ResourceLocation.tryParse(parts[0].trim());
        Optional<RpgStat> primary = RpgStat.parse(parts[1]);
        Optional<RpgStat> secondary = RpgStat.parse(parts[2]);
        return id == null || primary.isEmpty() || secondary.isEmpty() ? Optional.empty()
                : Optional.of(new SkillTreeDefinition(id, primary.get(), secondary.get()));
    }
}
