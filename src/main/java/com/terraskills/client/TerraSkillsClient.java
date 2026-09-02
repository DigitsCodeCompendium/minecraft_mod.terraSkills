package com.terraskills.client;

import com.terraskills.TerraSkills;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/** Client-only entry point that enables NeoForge's native Mods-menu config editor. */
@Mod(value = TerraSkills.MOD_ID, dist = Dist.CLIENT)
public final class TerraSkillsClient {
    public TerraSkillsClient(IEventBus modBus, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modBus.addListener(TerraSkillsClient::registerGuiLayers);
    }

    private static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(TerraSkills.MOD_ID, "skill_point_progress"), SkillPointHud::render);
    }
}
