package me.decoloured.meteorite.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public final class MeteoriteConfigController {
    private static final Logger log = LogManager.getLogger();

    private static final File configFile;
    private static final MeteoriteConfig defaults = new MeteoriteConfig();

    static {
        configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "meteorite.properties");
        try {
            if (configFile.createNewFile()) {
                persist(new MeteoriteConfig());
            }
        } catch (IOException e) {
            log.warn("Could not create configuration file");
        }
    }

    public MeteoriteConfigController() {
    }

    public static Screen getConfigScreen(MeteoriteConfig config, Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(new TranslatableText("meteorite.config.title"))
                .setSavingRunnable(() -> persist(config));
        builder.getOrCreateCategory(new TranslatableText("meteorite.config.category.general"))
                .addEntry(ConfigEntryBuilder.create()
                        .startSelector(new TranslatableText("meteorite.config.option.durabilityType"), MeteoriteConfig.DurabilityType.values(), config.durabilityType)
                        .setDefaultValue(MeteoriteConfig.DurabilityType.PERCENTAGE)
                        .setNameProvider(value -> {
                            if(value.equals(MeteoriteConfig.DurabilityType.PERCENTAGE)) {
                                return new TranslatableText("meteorite.config.option.durabilityType.percentage");
                            } else if(value.equals(MeteoriteConfig.DurabilityType.ABSOLUTE)) {
                                return new TranslatableText("meteorite.config.option.durabilityType.absolute");
                            }
                            return new LiteralText("Error");
                        })
                        .setSaveConsumer(value -> config.durabilityType = value)
                        .build())
                //.addEntry(ConfigEntryBuilder.create()
                //        .startBooleanToggle(new TranslatableText("meteorite.config.option.rainbow"), config.rainbow)
                //        .setDefaultValue(defaults.rainbow)
                //        .setSaveConsumer(value -> config.rainbow = value)
                //        .build())
                //.addEntry(ConfigEntryBuilder.create()
                //        .startColorField(new TranslatableText("meteorite.config.option.primary"), config.primary)
                //        .setDefaultValue(defaults.primary)
                //        .setSaveConsumer(value -> config.primary = value)
                //        .build())
                .addEntry(ConfigEntryBuilder.create()
                        .startSelector(new TranslatableText("meteorite.config.option.speedunit"), MeteoriteConfig.SpeedUnit.values(), config.speedUnit)
                        .setDefaultValue(MeteoriteConfig.SpeedUnit.METERSPERSECOND)
                        .setNameProvider(value -> {
                            if(value.equals(MeteoriteConfig.SpeedUnit.METERSPERSECOND)) {
                                return new TranslatableText("meteorite.config.option.speedunit.meterspersecond");
                            } else if(value.equals(MeteoriteConfig.SpeedUnit.KILOMETERSPERHOUR)) {
                                return new TranslatableText("meteorite.config.option.speedunit.kilometersperhour");
                            }
                            return new LiteralText("Error");
                        })
                        .setSaveConsumer(value -> config.speedUnit = value)
                        .build())
                .addEntry(ConfigEntryBuilder.create()
                        .startSelector(new TranslatableText("meteorite.config.option.textradarlocation"), MeteoriteConfig.TextRadarLocation.values(), config.textRadarLocation)
                        .setDefaultValue(MeteoriteConfig.TextRadarLocation.NORMAL)
                        .setNameProvider(value -> {
                            if(value.equals(MeteoriteConfig.TextRadarLocation.NORMAL)) {
                                return new TranslatableText("meteorite.config.option.textradarlocation.normal");
                            } else if(value.equals(MeteoriteConfig.TextRadarLocation.CROSSHAIR)) {
                                return new TranslatableText("meteorite.config.option.textradarlocation.crosshair");
                            }
                            return new LiteralText("Error");
                        })
                        .setSaveConsumer(value -> config.textRadarLocation = value)
                        .build());

        builder.getOrCreateCategory(new TranslatableText("meteorite.config.category.world"))

                .addEntry(ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableText("meteorite.config.option.showBiome"), config.biome)
                        .setDefaultValue(defaults.biome)
                        .setSaveConsumer(value -> config.biome = value)
                        .build())
                .addEntry(ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableText("meteorite.config.option.showPos"), config.pos)
                        .setDefaultValue(defaults.pos)
                        .setSaveConsumer(value -> config.pos = value)
                        .build())
                .addEntry(ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableText("meteorite.config.option.showNetherPos"), config.netherPos)
                        .setDefaultValue(defaults.netherPos)
                        .setSaveConsumer(value -> config.netherPos = value)
                        .build())
                    .addEntry(ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableText("meteorite.config.option.showLeftPos"), config.leftPos)
                        .setDefaultValue(defaults.leftPos)
                        .setSaveConsumer(value -> config.leftPos = value)
                        .build())
                .addEntry(ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableText("meteorite.config.option.showDirection"), config.direction)
                        .setDefaultValue(defaults.direction)
                        .setSaveConsumer(value -> config.direction = value)
                        .build())
                .addEntry(ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableText("meteorite.config.option.showWorldTime"), config.worldTime)
                        .setDefaultValue(defaults.worldTime)
                        .setSaveConsumer(value -> config.worldTime = value)
                        .build());
        builder.getOrCreateCategory(new TranslatableText("meteorite.config.category.player"))
                .addEntry(ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableText("meteorite.config.option.showEffects"), config.effects)
                        .setDefaultValue(defaults.effects)
                        .setSaveConsumer(value -> config.effects = value)
                        .build())
                .addEntry(ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableText("meteorite.config.option.showXP"), config.xp)
                        .setDefaultValue(defaults.xp)
                        .setSaveConsumer(value -> config.xp = value)
                        .build())
                .addEntry(ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableText("meteorite.config.option.showArmor"), config.armor)
                        .setDefaultValue(defaults.armor)
                        .setSaveConsumer(value -> config.armor = value)
                        .build())
                .addEntry(ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableText("meteorite.config.option.showArrow"), config.arrow)
                        .setDefaultValue(defaults.arrow)
                        .setSaveConsumer(value -> config.arrow = value)
                        .build())
                    .addEntry(ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableText("meteorite.config.option.showTotem"), config.totem)
                        .setDefaultValue(defaults.totem)
                        .setSaveConsumer(value -> config.totem = value)
                        .build())
                .addEntry(ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableText("meteorite.config.option.showDurability"), config.durability)
                        .setDefaultValue(defaults.durability)
                        .setSaveConsumer(value -> config.durability = value)
                        .build())
                .addEntry(ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableText("meteorite.config.option.showName"), config.name)
                        .setDefaultValue(defaults.name)
                        .setSaveConsumer(value -> config.name = value)
                        .build())
                .addEntry(ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableText("meteorite.config.option.showSpeed"), config.speed)
                        .setDefaultValue(defaults.speed)
                        .setSaveConsumer(value -> config.speed = value)
                        .build())
                .addEntry(ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableText("meteorite.config.option.showTextRadar"), config.textradar)
                        .setDefaultValue(defaults.textradar)
                        .setSaveConsumer(value -> config.textradar = value)
                        .build())
                .addEntry(ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableText("meteorite.config.option.showSaturation"), config.saturation)
                        .setDefaultValue(defaults.saturation)
                        .setSaveConsumer(value -> config.saturation = value)
                        .build());
        builder.getOrCreateCategory(new TranslatableText("meteorite.config.category.network"))
                .addEntry(ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableText("meteorite.config.option.showIP"), config.ip)
                        .setDefaultValue(defaults.ip)
                        .setSaveConsumer(value -> config.ip = value)
                        .build())
                .addEntry(ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableText("meteorite.config.option.showPing"), config.ping)
                        .setDefaultValue(defaults.ping)
                        .setSaveConsumer(value -> config.ping = value)
                        .build())
                .addEntry(ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableText("meteorite.config.option.showTPS"), config.tps)
                        .setDefaultValue(defaults.tps)
                        .setSaveConsumer(value -> config.tps = value)
                        .build());
        builder.getOrCreateCategory(new TranslatableText("meteorite.config.category.client"))
                .addEntry(ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableText("meteorite.config.option.showTime"), config.time)
                        .setDefaultValue(defaults.time)
                        .setSaveConsumer(value -> config.time = value)
                        .build())
                .addEntry(ConfigEntryBuilder.create()
                        .startBooleanToggle(new TranslatableText("meteorite.config.option.showFPS"), config.fps)
                        .setDefaultValue(defaults.fps)
                        .setSaveConsumer(value -> config.fps = value)
                        .build());
        return builder.build();
    }

    public static void load(MeteoriteConfig config) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(configFile));
            //World
            config.biome = Boolean.parseBoolean(props.getProperty("world.biome"));
            config.pos = Boolean.parseBoolean(props.getProperty("world.pos"));
            config.netherPos = Boolean.parseBoolean(props.getProperty("world.netherPos"));
            config.leftPos = Boolean.parseBoolean(props.getProperty("world.leftPos"));
            config.direction = Boolean.parseBoolean(props.getProperty("world.direction"));
            config.worldTime = Boolean.parseBoolean(props.getProperty("world.worldTime"));

            //Player
            config.xp = Boolean.parseBoolean(props.getProperty("player.xp"));
            config.effects = Boolean.parseBoolean(props.getProperty("player.effects"));
            config.armor = Boolean.parseBoolean(props.getProperty("player.armor"));
            config.arrow = Boolean.parseBoolean(props.getProperty("player.arrow"));
            config.totem = Boolean.parseBoolean(props.getProperty("player.totem"));
            config.durability = Boolean.parseBoolean(props.getProperty("player.durability"));
            config.name = Boolean.parseBoolean(props.getProperty("player.name"));
            config.speed = Boolean.parseBoolean(props.getProperty("player.speed"));
            config.textradar = Boolean.parseBoolean(props.getProperty("player.textradar"));
            config.saturation = Boolean.parseBoolean(props.getProperty("player.saturation"));

            //Network
            config.ip = Boolean.parseBoolean(props.getProperty("network.ip"));
            config.ping = Boolean.parseBoolean(props.getProperty("network.ping"));
            config.tps = Boolean.parseBoolean(props.getProperty("network.tps"));

            //Client
            config.fps = Boolean.parseBoolean(props.getProperty("client.fps"));
            config.time = Boolean.parseBoolean(props.getProperty("client.time"));
            //config.rainbow = Boolean.parseBoolean(props.getProperty("client.rainbow"));
            //try {
            //    config.primary = Integer.parseInt(props.getProperty("general.primary"));
            //} catch (Exception e) {
            //    config.primary = 0xFFFFFF;
            //}
            config.durabilityType = MeteoriteConfig.DurabilityType.valueOf(props.getProperty("general.durabilitytype", "PERCENTAGE"));
            config.speedUnit = MeteoriteConfig.SpeedUnit.valueOf(props.getProperty("general.speedunit", "METERSPERSECOND"));
            config.textRadarLocation = MeteoriteConfig.TextRadarLocation.valueOf(props.getProperty("general.textradarlocation", "NORMAL"));
        } catch (IOException e) {
            log.warn("Could not load configuration settings");
        }
    }

    private static void persist(MeteoriteConfig config) {
        Properties props = new Properties();
        //World
        props.setProperty("world.biome", String.valueOf(config.biome));
        props.setProperty("world.pos", String.valueOf(config.pos));
        props.setProperty("world.netherPos", String.valueOf(config.netherPos));
        props.setProperty("world.leftPos", String.valueOf(config.leftPos));
        props.setProperty("world.direction", String.valueOf(config.direction));
        props.setProperty("world.worldTime", String.valueOf(config.worldTime));
        //Player
        props.setProperty("player.xp", String.valueOf(config.xp));
        props.setProperty("player.effects", String.valueOf(config.effects));
        props.setProperty("player.armor", String.valueOf(config.armor));
        props.setProperty("player.arrow", String.valueOf(config.arrow));
        props.setProperty("player.durability", String.valueOf(config.durability));
        props.setProperty("player.name", String.valueOf(config.name));
        props.setProperty("player.speed", String.valueOf(config.speed));
        props.setProperty("player.textradar", String.valueOf(config.textradar));
        props.setProperty("player.saturation", String.valueOf(config.saturation));
        //Network
        props.setProperty("network.ip", String.valueOf(config.ip));
        props.setProperty("network.ping", String.valueOf(config.ping));
        props.setProperty("network.tps", String.valueOf(config.tps));
        //Client
        props.setProperty("client.fps", String.valueOf(config.fps));
        props.setProperty("client.time", String.valueOf(config.time));
        //General
        //props.setProperty("general.rainbow", String.valueOf(config.rainbow));
        //props.setProperty("general.primary", String.valueOf(config.primary));
        props.setProperty("general.durabilitytype", String.valueOf(config.durabilityType));
        props.setProperty("general.speedunit", String.valueOf(config.speedUnit));
        props.setProperty("general.textradarlocation", String.valueOf(config.textRadarLocation));
        try {
            configFile.createNewFile();
            props.store(new FileOutputStream(configFile), "Meteorite Config");
        } catch (IOException e) {
            log.warn("Could not save configuration settings");
        }
    }

}
