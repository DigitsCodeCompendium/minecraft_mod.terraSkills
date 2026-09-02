package com.terraskills;

import com.mojang.logging.LogUtils;
import com.terraskills.config.TerraSkillsConfig;
import com.terraskills.config.TerraSkillsClientConfig;
import com.terraskills.network.TerraSkillsNetwork;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(TerraSkills.MOD_ID)
public final class TerraSkills {
    public static final String MOD_ID = "terraskills";
    public static final Logger LOGGER = LogUtils.getLogger();

    public TerraSkills(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.SERVER, TerraSkillsConfig.SPEC);
        container.registerConfig(ModConfig.Type.CLIENT, TerraSkillsClientConfig.SPEC);
        modBus.addListener(TerraSkillsNetwork::registerPayloads);
        LOGGER.info("TerraSkills loaded: TFC nutrition and Pufferfish Skills bridge is available");
    }
}
