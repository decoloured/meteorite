package me.decoloured.meteorite.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.decoloured.meteorite.Meteorite;

public class MeteoriteModMenuEntry implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> MeteoriteConfigController.getConfigScreen(Meteorite.config(), parent);
    }
}

