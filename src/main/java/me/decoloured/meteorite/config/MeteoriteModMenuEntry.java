package me.decoloured.meteorite.config;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.decoloured.meteorite.Meteorite;

public class MeteoriteModMenuEntry implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> MeteoriteConfigController.getConfigScreen(Meteorite.config(), parent);
    }
}

