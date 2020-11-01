package me.decoloured.meteorite;

import me.decoloured.meteorite.config.MeteoriteConfig;
import me.decoloured.meteorite.config.MeteoriteConfigController;
import net.fabricmc.api.ClientModInitializer;

public class Meteorite implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MeteoriteConfigController.load(config);
    }
    
    private static final MeteoriteConfig config = new MeteoriteConfig();

    public static MeteoriteConfig config() {
        return config;
    }
}
