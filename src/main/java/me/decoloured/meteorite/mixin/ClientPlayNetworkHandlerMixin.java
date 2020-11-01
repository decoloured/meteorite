package me.decoloured.meteorite.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.decoloured.meteorite.client.DrawUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
  final DrawUI drawUI = new DrawUI(MinecraftClient.getInstance());
  @Inject(method = "onWorldTimeUpdate", at = @At("RETURN"))
  private void onTimeUpdate(net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket packetIn, CallbackInfo ci) {
    drawUI.updateTPS(packetIn.getTime());
  }
}
