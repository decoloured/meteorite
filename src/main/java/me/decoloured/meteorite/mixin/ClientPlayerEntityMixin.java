package me.decoloured.meteorite.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.decoloured.meteorite.client.DrawUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
  private MinecraftClient client = MinecraftClient.getInstance();
  @Inject(method = "tick", at = @At("HEAD"))
  private void test(CallbackInfo ci) {
    DrawUI.getArrowCount(client.player);
    DrawUI.getTotemCount(client.player);
    DrawUI.updateNearbyPlayers(client);
  }
}
