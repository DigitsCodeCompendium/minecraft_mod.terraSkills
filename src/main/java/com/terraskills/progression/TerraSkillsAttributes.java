package com.terraskills.progression;

import com.terraskills.TerraSkills;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.Map;

/** Minecraft attributes exposed for Origins and other attribute-based integrations. */
public final class TerraSkillsAttributes {
    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, TerraSkills.MOD_ID);
    private static final Map<RpgStat, DeferredHolder<Attribute, Attribute>> BY_STAT = new EnumMap<>(RpgStat.class);

    public static final DeferredHolder<Attribute, Attribute> MIGHT = register(RpgStat.MIGHT);
    public static final DeferredHolder<Attribute, Attribute> FINESSE = register(RpgStat.FINESSE);
    public static final DeferredHolder<Attribute, Attribute> ENDURANCE = register(RpgStat.ENDURANCE);
    public static final DeferredHolder<Attribute, Attribute> INTELLIGENCE = register(RpgStat.INTELLIGENCE);
    public static final DeferredHolder<Attribute, Attribute> INSTINCT = register(RpgStat.INSTINCT);

    private TerraSkillsAttributes() {}

    public static void register(IEventBus modBus) {
        ATTRIBUTES.register(modBus);
        modBus.addListener(TerraSkillsAttributes::addPlayerAttributes);
    }

    public static Holder<Attribute> get(RpgStat stat) {
        return BY_STAT.get(stat);
    }

    private static DeferredHolder<Attribute, Attribute> register(RpgStat stat) {
        DeferredHolder<Attribute, Attribute> holder = ATTRIBUTES.register(stat.id(), () ->
                new RangedAttribute("attribute.name." + TerraSkills.MOD_ID + "." + stat.id(), 0, 0, 1_000_000)
                        .setSyncable(true));
        BY_STAT.put(stat, holder);
        return holder;
    }

    private static void addPlayerAttributes(EntityAttributeModificationEvent event) {
        for (RpgStat stat : RpgStat.values()) event.add(EntityType.PLAYER, get(stat));
    }
}
