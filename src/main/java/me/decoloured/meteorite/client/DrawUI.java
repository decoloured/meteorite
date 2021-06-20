package me.decoloured.meteorite.client;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.text.WordUtils;

import me.decoloured.meteorite.Meteorite;
import me.decoloured.meteorite.config.MeteoriteConfig;
import me.decoloured.meteorite.config.MeteoriteConfig.PrimaryColorType;
import me.decoloured.meteorite.config.MeteoriteConfig.SecondaryColorType;
import me.decoloured.meteorite.config.MeteoriteConfig.TextRadarLocation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class DrawUI implements Drawable {
  final MinecraftClient client;
  final TextRenderer text;
  MeteoriteConfig config;
  private static DrawUI instance;
  int height;
  private ClientPlayerEntity player;
  public static double serverTPS;
  public static double serverMSPT;
  private long lastServerTick;
  private static long lastServerTimeUpdate;
  private static int arrowCount = 0;
  private static int totemCount = 0;
  float color = 0.0F;
  float offset = 0.0F;
  float offsetAmount = 0.05F;
  static List<AbstractClientPlayerEntity> nearbyPlayers;

  public static DrawUI getInstance() {
    return instance;
  }

  public DrawUI(MinecraftClient client) {
    this.client = client;
    this.text = client.textRenderer;
  }

  public void draw() {
    this.player = this.client.player;
    this.config = Meteorite.config();

    this.drawRightStats();
    this.drawLeftStats();

    if (this.config.lag) {
      Float lagMeter = (System.nanoTime() - DrawUI.lastServerTimeUpdate)/1000000000F;
      if (lagMeter >= 2) {
        MatrixStack stack = new MatrixStack();
        String lag = "Server not responding ";
        String lag2 = String.format("%.1fs", lagMeter);
        chromaText(stack, lag + lag2, this.client.getWindow().getScaledWidth() / 2 - this.text.getWidth(lag + lag2) / 2, 2, 0, 0.01F);  
      }
    }

    this.client.getProfiler().pop();
    color = System.nanoTime() / 50000000000F;
  }

  private void drawRightStats() {
    if (Meteorite.config().armor) {
      drawEquipmentInfo();
    }
    if (Meteorite.config().itemCount) {
      drawItemCount(player);
    }

    offset = 0.0F;

    int scaleWidth = this.client.getWindow().getScaledWidth();
    MatrixStack stack = new MatrixStack();

    height = this.text.fontHeight + 2;
    int scaleHeight = this.client.getWindow().getScaledHeight();
    if (client.currentScreen instanceof ChatScreen) {
      scaleHeight -= 12;
    }
    if (this.client.player != null && this.config.effects) {
      Map<StatusEffect, StatusEffectInstance> effects = this.client.player.getActiveStatusEffects();
      for (Map.Entry<StatusEffect, StatusEffectInstance> effect : effects.entrySet()) {
        String effectName = I18n.translate(effect.getKey().getTranslationKey());
        // String effectLvl = "";
        // if (effect.getValue().getAmplifier() >= 1 && effect.getValue().getAmplifier()
        // <= 9) {
        // effectLvl = I18n.translate("enchantment.level." +
        // (effect.getValue().getAmplifier() + 1));
        // }
        String effectDuration = secondsToString(effect.getValue().getDuration() / 20);

        int color = effect.getKey().getColor();

        String effectString = effectDuration;
        String effectString2 = String.format(" %s %s", effectName, effect.getValue().getAmplifier() + 1);

        drawWithShadowconcat(stack, effectString, scaleWidth - this.text.getWidth(effectString2) - 2,
            scaleHeight - height, color, stack, effectString2, secondaryColor(), false);
        scaleHeight -= height;
        // offset += offsetAmount;
      }
    }
    if (!this.config.leftPos) {
      if (this.config.pos) {
        String coords = "XYZ ";
        String coords2 = getCoords();
        drawWithShadowconcat(stack, coords, scaleWidth - this.text.getWidth(coords2) - 2, scaleHeight - height,
          primaryColor(), stack, coords2, secondaryColor(), false);
        scaleHeight -= height;
        offset += offsetAmount;
      }
      if (this.config.direction) {
        String direction = getDirection();
        String direction2 = String.format("[%.1f, %.1f]", MathHelper.wrapDegrees(this.player.getYaw()),
            MathHelper.wrapDegrees(this.player.getPitch()));
        drawWithShadowconcat(stack, direction, scaleWidth - this.text.getWidth(direction2) - 2, scaleHeight - height,
          primaryColor(), stack, direction2, secondaryColor(), false);
        scaleHeight -= height;
        offset += offsetAmount;
      }
    }
    if (this.config.biome) {
      String biome = "Biome ";
      String biome2 = this.client.world.getRegistryManager().get(Registry.BIOME_KEY)
          .getId(this.client.world.getBiome(this.player.getBlockPos())).toString();
      biome2 = WordUtils.capitalize(biome2.substring(10).replace("_", " "));
      drawWithShadowconcat(stack, biome, scaleWidth - this.text.getWidth(biome2) - 2, scaleHeight - height,
        primaryColor(), stack, biome2, secondaryColor(), false);
      scaleHeight -= height;
      offset += offsetAmount;
    }
    if (this.config.saturation) {
      String saturation = "Saturation ";
      String saturation2 = String.format("%.1f", player.getHungerManager().getSaturationLevel());
      drawWithShadowconcat(stack, saturation, scaleWidth - this.text.getWidth(saturation2) - 2, scaleHeight - height,
        primaryColor(), stack, saturation2, secondaryColor(), false);
      scaleHeight -= height;
      offset += offsetAmount;
    }
    if (this.config.durability) {
      ItemStack hand = player.getMainHandStack();
      int maxdamage = hand.getMaxDamage();
      int damage = maxdamage - hand.getDamage();
      String durability = "Durability ";
      String durability2 = String.valueOf(damage);
      if (hand.isDamageable()) {
        drawWithShadowconcat(stack, durability, scaleWidth - this.text.getWidth(durability2) - 2, scaleHeight - height,
          primaryColor(), stack, durability2, secondaryColor(), false);
        scaleHeight -= height;
        offset += offsetAmount;
      }
    }
    if (this.config.speed) {
      double dx = this.player.getX() - this.player.lastRenderX;
      double dy = this.player.getY() - this.player.lastRenderY;
      double dz = this.player.getZ() - this.player.lastRenderZ;
      double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
      String speed = "Speed ";
      String speed2 = "";
      if (this.config.speedUnit == MeteoriteConfig.SpeedUnit.KILOMETERSPERHOUR) {
        speed2 = String.format("%.1f km/h", dist * 72);
      } else {
        speed2 = String.format("%.1f m/s", dist * 20);
      }
      drawWithShadowconcat(stack, speed, scaleWidth - this.text.getWidth(speed2) - 2, scaleHeight - height,
        primaryColor(), stack, speed2, secondaryColor(), false);
      scaleHeight -= height;
      offset += offsetAmount;
    }
    if (this.config.worldTime) {
      long totalTime = this.player.getEntityWorld().getTimeOfDay();
      // long dayCount = (int) (totalTime / 24000) + 1;
      int dayTicks = (int) (totalTime % 24000);
      int hour24 = (int) ((dayTicks / 1000) + 6) % 24;
      int hour = (int) ((dayTicks / 1000) + 6) % 12;
      if (hour == 0) {
        hour = 12;
      }
      int min = (int) (dayTicks / 16.666666) % 60;
      String ahour;

      if (hour24 < 12) {
        ahour = "AM";
      } else {
        ahour = "PM";
      }

      String time = "Time ";
      String time2 = String.format("%02d:%02d %s", hour, min, ahour);
      if (dayTicks > 12500 && dayTicks < 23400) {
        time2 = Formatting.UNDERLINE + time2;
      }
      drawWithShadowconcat(stack, time, scaleWidth - this.text.getWidth(time2) - 2, scaleHeight - height,
        primaryColor(), stack, time2, secondaryColor(), false);
      scaleHeight -= height;
      offset += offsetAmount;
    }
    if (this.config.arrow) {
      String arrow = "Arrows ";
      String arrow2 = String.format("%s", DrawUI.arrowCount);
      if (projectile()) {
        drawWithShadowconcat(stack, arrow, scaleWidth - this.text.getWidth(arrow2) - 2, scaleHeight - height,
          primaryColor(), stack, arrow2, secondaryColor(), false);
        scaleHeight -= height;
        offset += offsetAmount;
      }
    }
    if (this.config.totem) {
      String totem = "Totems ";
      String totem2 = String.format("%s", DrawUI.totemCount);
      if (DrawUI.totemCount != 0) {
        drawWithShadowconcat(stack, totem, scaleWidth - this.text.getWidth(totem2) - 2, scaleHeight - height,
          primaryColor(), stack, totem2, secondaryColor(), false);
        scaleHeight -= height;
        offset += offsetAmount;
      }
    }
    if (this.config.tps) {
      String tps = "TPS ";
      String tps2 = String.format("%.2f", DrawUI.serverTPS);
      drawWithShadowconcat(stack, tps, scaleWidth - this.text.getWidth(tps2) - 2, scaleHeight - height,
        primaryColor(), stack, tps2, secondaryColor(), false);
      scaleHeight -= height;
      offset += offsetAmount;
    }
    if (this.config.ping) {
      if (this.client.getCurrentServerEntry() != null) {
        PlayerListEntry entry = this.client.getNetworkHandler().getPlayerListEntry(this.player.getUuid());
        if (entry != null) {
          String ping = "Ping ";
          String ping2 = String.valueOf(entry.getLatency()) + "ms";
          drawWithShadowconcat(stack, ping, scaleWidth - this.text.getWidth(ping2) - 2, scaleHeight - height,
            primaryColor(), stack, ping2, secondaryColor(), false);
          scaleHeight -= height;
          offset += offsetAmount;
        }
      }
    }
    if (this.config.fps) {

      String fps = "FPS ";
      String fps2 = this.client.fpsDebugString.substring(0, this.client.fpsDebugString.indexOf(" "));
      drawWithShadowconcat(stack, fps, scaleWidth - this.text.getWidth(fps2) - 2, scaleHeight - height,
        primaryColor(), stack, fps2, secondaryColor(), false);
      scaleHeight -= height;
      offset += offsetAmount;
    }
    if (this.config.xp) {
      String xp = "XP " + this.player.experienceLevel;
      String xp2 =  "/" + this.player.totalExperience;
      if (!this.player.isCreative()) {
        drawWithShadowconcat(stack, xp, scaleWidth - this.text.getWidth(xp2) - 2, scaleHeight - height,
          primaryColor(), stack, xp2, secondaryColor(), false);
        scaleHeight -= height;
        offset += offsetAmount;
      }
    }
    if (this.config.textradar) {
      scaleHeight -= height;
      int radarHeight = this.client.getWindow().getScaledHeight() / 2 + this.text.fontHeight + 4;
      if (DrawUI.nearbyPlayers == null) {
        return;
      }
      for (AbstractClientPlayerEntity nPlayer : DrawUI.nearbyPlayers) {
        if (nPlayer.getEntityName() != this.player.getEntityName()) {
          String player = "";
          String player2 = "";
          if (Math.round(nPlayer.getY()) > Math.round(this.player.getY())) {
            player += "+ ";
          }
          if (Math.round(nPlayer.getY()) < Math.round(this.player.getY())) {
            player += "- ";
          }
          if (nPlayer.isCreative()) {
            player += "[C] ";
          }
          player += String.format("%.1f %s", nPlayer.getHealth() + nPlayer.getAbsorptionAmount(),
              nPlayer.getEntityName());
          try {
            int latency = this.client.getNetworkHandler().getPlayerListEntry(nPlayer.getEntityName()).getLatency();
            player2 = String.format(" %sms %.0fm", latency, this.player.distanceTo(nPlayer));
          } catch (Exception e) {
            continue;
          }

          if (this.config.textRadarLocation == TextRadarLocation.NORMAL) {
            drawWithShadowconcat(stack, player, scaleWidth - this.text.getWidth(player2) - 2, scaleHeight - height,
              primaryColor(), stack, player2, secondaryColor(), false);
            scaleHeight -= height;
          } else {
            drawWithShadowconcat(stack, player, this.client.getWindow().getScaledWidth() / 2 + 2, radarHeight - height,
              primaryColor(), stack, player2, secondaryColor(), true);
            radarHeight += height;
          }
          offset += offsetAmount;
        }
      }
    }
  }

  private void drawLeftStats() {
    MatrixStack stack = new MatrixStack();
    height = this.text.fontHeight + 2;
    offset = 0.0F;
    int scaledHeight = this.client.getWindow().getScaledHeight();
    int scaleWidth = 2;
    if (client.currentScreen instanceof ChatScreen) {
      scaledHeight -= 12;
    }
    if (this.config.leftPos) {
      if (this.config.pos) {
        String coords = "XYZ ";
        String coords2 = getCoords();
        drawWithShadowconcat(stack, coords, scaleWidth, scaledHeight - height, 
          primaryColor(), stack, coords2, secondaryColor(), true);
        scaledHeight -= height;
        offset += offsetAmount;
      }
      if (this.config.direction) {
        String direction = getDirection();
        String direction2 = String.format("[%.1f, %.1f]", MathHelper.wrapDegrees(this.player.getYaw()),
            MathHelper.wrapDegrees(this.player.getPitch()));
        drawWithShadowconcat(stack, direction, scaleWidth, scaledHeight - height, 
          primaryColor(), stack, direction2, secondaryColor(), true);
        scaledHeight -= height;
        offset += offsetAmount;
      }
    }
    if (this.config.ip) {
      if (this.client.getCurrentServerEntry() != null) {
        this.player.getEntityWorld().getServer();
        String ip = "IP ";
        String ip2 = this.client.getCurrentServerEntry().address;
        drawWithShadowconcat(stack, ip, scaleWidth, scaledHeight - height, 
          primaryColor(), stack, ip2, secondaryColor(), true);
        scaledHeight -= height;
        offset += offsetAmount;
      }
    }
    if (this.config.time) {
      SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
      Date date = new Date();
      String time = "Time ";
      String time2 = formatter.format(date);
      drawWithShadowconcat(stack, time, scaleWidth, scaledHeight - height, 
        primaryColor(), stack, time2, secondaryColor(), true);
      scaledHeight -= height;
      offset += offsetAmount;
    }
    if (this.config.name) {
      String name = "Player ";
      String name2 = player.getEntityName();
      // chromaText(stack, name2, 4, 4, offset);
      // GL11.glPushMatrix();
      // GL11.glScaled(0.666666666, 0.666666666, 0.666666666);
      // this.text.drawWithShadow(stack, "v2.1", (this.text.getWidth(name2) + 5) *
      // 1.5F, 6, 0xFFFFFF);
      // GL11.glPopMatrix();
      drawWithShadowconcat(stack, name, scaleWidth, scaledHeight - height, 
        primaryColor(), stack, name2, secondaryColor(), true);
      scaledHeight -= height;
      offset += offsetAmount;
    }
    offset = 0.0F;
  }

  private void drawWithShadowconcat(MatrixStack matrix1, String text1, float x, float y, int color1,
      MatrixStack matrix2, String text2, int color2, boolean left) {
    if (left) {
      // chromaText(matrix1, text1, x, y, offset);
      this.text.drawWithShadow(matrix1, text1, x, y, color1);
      //this.text.draw(matrix1, text1, x + 0.333333F, y + 0.333333F, shadowColor());
      //this.text.draw(matrix1, text1, x, y, color1);
      this.text.drawWithShadow(matrix2, text2, x + this.text.getWidth(text1), y,
      color2);
      //this.text.draw(matrix2, text2, x + this.text.getWidth(text1) + 0.333333F, y + 0.333333F, 0x2A2A2A);
      //this.text.draw(matrix2, text2, x + this.text.getWidth(text1), y, color2);
    } else {
      // chromaText(matrix1, text1, x - this.text.getWidth(text1), y, offset);
      this.text.drawWithShadow(matrix1, text1, x - this.text.getWidth(text1), y,
      color1);
      //this.text.draw(matrix1, text1, x - this.text.getWidth(text1) + 0.333333F, y + 0.333333F, shadowColor());
      //this.text.draw(matrix1, text1, x - this.text.getWidth(text1), y, color1);
      this.text.drawWithShadow(matrix2, text2, x, y, color2);
      //this.text.draw(matrix2, text2, x + 0.333333F, y + 0.333333F, 0x2A2A2A);
      //this.text.draw(matrix2, text2, x, y, color2);
    }
  }

  private void drawItemCount(ClientPlayerEntity player) {
    MatrixStack stack = new MatrixStack();
    ItemStack item = player.getInventory().getMainHandStack();
    if (player.isCreative() || player.isSpectator() || !item.isStackable() || item.getItem().toString() == "air") {
      return;
    }
    int size = player.getInventory().size();
    int count = 0;
    for (int i = 0; i < size; i++) {
      String j = player.getInventory().getStack(i).getItem().toString();
      if (j == item.getItem().toString()) {
        count += player.getInventory().getStack(i).getCount();
      }
    }
    this.text.drawWithShadow(stack, "" + count, (this.client.getWindow().getScaledWidth() - this.text.getWidth("" + count)) / 2, this.client.getWindow().getScaledHeight() - 45, primaryColor());
  }

  private void drawEquipmentInfo() {
    MatrixStack stack = new MatrixStack();
    List<ItemStack> equippedItems = new ArrayList<>(this.player.getInventory().armor);
    equippedItems = Lists.reverse(equippedItems);
    equippedItems.add(this.player.getOffHandStack());

    int itemWidth = this.client.getWindow().getScaledWidth() / 2 + 10;

    int itemSize = this.text.fontHeight + 4;
    int i = 0;
    // Draw in order Helmet -> Breastplate -> Leggings -> Boots
    for (ItemStack equippedItem : equippedItems) {
      if (equippedItem.getItem().equals(Blocks.AIR.asItem()) || player.isSpectator()) {
        continue;
      }
      i++;
      Integer x = itemWidth + i * 8 - 9;
      Integer height = this.client.getWindow().getScaledHeight() - 55;
      float textX = ((i - 2) * 8) + itemWidth + 8;
      if (this.player.isCreative()) {
        height = this.client.getWindow().getScaledHeight() - 40;
      }
      if (player.isSubmergedIn(FluidTags.WATER)) {
        height = this.client.getWindow().getScaledHeight() - 65;
      }

      this.client.getItemRenderer().renderInGuiWithOverrides(equippedItem, x, height);
      this.client.getItemRenderer().renderGuiItemOverlay(this.text, equippedItem, x, height);
      int durability = equippedItem.getMaxDamage() - equippedItem.getDamage();
      if (equippedItem.isDamageable()) {
        stack.push();
        stack.translate(textX, 0, 0);
        stack.scale(2F/3F, 2F/3F, 2F/3F);
        float t = 10.5F - this.text.getWidth(durability(durability, equippedItem.getMaxDamage())) / 2;
        // chromaText(stack, String.format("%s", durability), (x.floatValue() * 1.5F) +
        // (4 - String.valueOf(durability).length()) * 3, (height.floatValue() - 6F) *
        // 1.5F, offset);

        this.text.drawWithShadow(stack, durability(durability, equippedItem.getMaxDamage()),
          t, (height.floatValue() - 6F) * 1.5F, primaryColor());
        //this.text.draw(stack, durability(durability, equippedItem.getMaxDamage()), t + 0.333333F,
        //    (height.floatValue() - 6F) * 1.5F + 0.333333F, shadowColor());
        //this.text.draw(stack, durability(durability, equippedItem.getMaxDamage()), t, (height.floatValue() - 6F) * 1.5F,
        //  Color.HSBtoRGB(color + offset, 0.5F, 1.0F));

        stack.pop();
      }
      offset += 0.05F;
      itemWidth += itemSize;
    }
    offset = 0.0F;
  }

  private int primaryColor() {
    if (config.primaryColorType == PrimaryColorType.RAINBOW) {
      return Color.HSBtoRGB(color + offset, 0.5F, 1.0F);
    } else {
      return config.primary;
    }
  }

  private int secondaryColor() {
    if (config.secondaryColorType == SecondaryColorType.RAINBOW) {
      return Color.HSBtoRGB(color + offset, 0.5F, 1.0F);
    } else {
      return config.secondary;
    }
  }

  private String zeroPadding(int number) {
    return (number >= 10) ? Integer.toString(number) : String.format("0%s", number);
  }

  private String secondsToString(int pTime) {
    final int min = pTime / 60;
    final int sec = pTime - (min * 60);

    final String strMin = zeroPadding(min);
    final String strSec = zeroPadding(sec);
    return String.format("%s:%s", strMin, strSec);
  }

  private String getCoords() {
    double x = player.getX(), z = player.getZ();
    double altX = player.getX() / 8, altZ = player.getZ() / 8;

    if (player.getEntityWorld().getRegistryKey().equals(World.NETHER)) {
      altX = player.getX() * 8;
      altZ = player.getZ() * 8;
    }

    if (player.getEntityWorld().getRegistryKey().equals(World.END) || !this.config.netherPos) {
      return String.format("%.1f %.1f %.1f", x, player.getY(), z);
    }
    return String.format("%.1f %.1f %.1f [%.1f %.1f]", x, player.getY(), z, altX, altZ);
  }

  private boolean projectile() {
    if (player.getMainHandStack().getItem().toString().contains("bow")) {
      if (player.getMainHandStack().getEnchantments().toString().contains("infinity")) {
        return false;
      }
      return true;
    }
    return false;
  }

  public static void getArrowCount(ClientPlayerEntity player) {
    int size = player.getInventory().size();
    int c = 0;
    for (int i = 0; i < size; i++) {
      String j = player.getInventory().getStack(i).getItem().toString();
      if (j == "arrow" || j == "spectral_arrow" || j == "tipped_arrow") {
        c += player.getInventory().getStack(i).getCount();
      }
    }
    DrawUI.arrowCount = c;
  }

  public static void getTotemCount(ClientPlayerEntity player) {
    int size = player.getInventory().size();
    int c = 0;
    for (int i = 0; i < size; i++) {
      String j = player.getInventory().getStack(i).getItem().toString();
      if (j == "totem_of_undying") {
        c += player.getInventory().getStack(i).getCount();
      }
    }
    DrawUI.totemCount = c;
  }

  private String getDirection() {
    Direction facing = this.player.getHorizontalFacing();
    // float yaw = this.player.yaw;
    // float pitch = this.player.pitch;
    String dir;
    switch (facing) {
      case NORTH:
        dir = "-Z";
        break;
      case SOUTH:
        dir = "+Z";
        break;
      case WEST:
        dir = "-X";
        break;
      case EAST:
        dir = "+X";
        break;
      default:
        dir = "Invalid";
    }
    return String.format("%s %s ", facing.name(), dir);
  }

  public void updateTPS(long totalWorldTime) {
    long currentTime = System.nanoTime();
    long elapsedTicks = totalWorldTime - this.lastServerTick;
    if (elapsedTicks > 0) {
      DrawUI.serverMSPT = ((double) (currentTime - DrawUI.lastServerTimeUpdate) / (double) elapsedTicks) / 1000000D;
      DrawUI.serverTPS = DrawUI.serverMSPT <= 50 ? 20D : (1000D / DrawUI.serverMSPT);
    }
    this.lastServerTick = totalWorldTime;
    DrawUI.lastServerTimeUpdate = currentTime;
  }

  String durability(int damage, int maxdamage) {
    if (Meteorite.config().durabilityType == MeteoriteConfig.DurabilityType.ABSOLUTE) {
      return String.valueOf(damage);
    } else {
      return String.format("%.0f", (double) damage / maxdamage * 100);
    }
  }

  void chromaText(MatrixStack stack, String text, float x, float y, float offset, float offsetAmount) {
    float j = offset;
    for (int i = 0; i < text.length(); i++) {
      this.text.draw(stack, text.substring(i), x + this.text.getWidth(text.substring(0, i)) + 0.333333F, y + 0.333333F,
        Color.HSBtoRGB(color + j, 0.5F, 0.25F));
      this.text.draw(stack, text.substring(i), x + this.text.getWidth(text.substring(0, i)), y,
        Color.HSBtoRGB(color + j, 0.5F, 1.0F));
      j -= offsetAmount;
    }
  }

  public static void updateNearbyPlayers(MinecraftClient client) {
    DrawUI.nearbyPlayers = client.world.getPlayers();
    Collections.sort(DrawUI.nearbyPlayers, new Comparator<AbstractClientPlayerEntity>() {
      @Override
      public int compare(AbstractClientPlayerEntity p1, AbstractClientPlayerEntity p2) {
        return Math.round(p1.distanceTo(client.player) - p2.distanceTo(client.player));
      }
    });
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) { }
}
