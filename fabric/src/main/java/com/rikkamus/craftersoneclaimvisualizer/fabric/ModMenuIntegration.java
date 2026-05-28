package com.rikkamus.craftersoneclaimvisualizer.fabric;

import com.rikkamus.craftersoneclaimvisualizer.config.ClothConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfigClient;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfigClient.getConfigScreen(ClothConfig.class, parent).get();
    }

}
