package com.rikkamus.craftersoneclaimvisualizer.fabric;

import com.rikkamus.craftersoneclaimvisualizer.config.ClothConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;

public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(ClothConfig.class, parent).get();
    }

}
