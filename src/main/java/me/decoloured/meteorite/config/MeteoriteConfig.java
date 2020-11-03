package me.decoloured.meteorite.config;

public class MeteoriteConfig {
    //World
    public boolean biome = true;
    public boolean pos = true;
    public boolean netherPos = true;
    public boolean leftPos = true;
    public boolean direction = true;
    public boolean worldTime = true;

    //Player
    public boolean effects = true;
    public boolean armor = true;
    public boolean durability = true;
    public boolean arrow = true;
    public boolean totem = true;
    public boolean xp = true;
    public boolean name = true;
    public boolean speed = true;
    public boolean textradar = true;
    public boolean saturation = true;

    //Network
    public boolean ip = true;
    public boolean ping = true;
    public boolean tps = true;
		public boolean lag = true;

    //Client
    public boolean fps = true;
    public boolean time = true;

    //General
    public boolean rainbow = true;
    public int primary = 0xFFFFFF;
    public int secondary = 0xB5B5B5;
    public DurabilityType durabilityType = DurabilityType.PERCENTAGE;
    public SpeedUnit speedUnit = SpeedUnit.METERSPERSECOND;
    public TextRadarLocation textRadarLocation = TextRadarLocation.NORMAL;

    public enum TextRadarLocation {
        CROSSHAIR,
        NORMAL
    }
    
    public enum SpeedUnit {
        METERSPERSECOND,
        KILOMETERSPERHOUR
    }

    public enum DurabilityType {
        PERCENTAGE,
        ABSOLUTE
    }
}
