package me.decoloured.meteorite.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.decoloured.meteorite.Meteorite;
import me.decoloured.meteorite.client.DrawUI;

@Environment(EnvType.CLIENT)
@Mixin(value = InGameHud.class)
public class ClientMixin {
    private DrawUI drawUI;
    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "<init>(Lnet/minecraft/client/MinecraftClient;)V", at = @At(value = "RETURN"))
    private void onInit(MinecraftClient client, CallbackInfo ci) {
        // Start Mixin
        System.out.println("initializing meteorite...");
        this.drawUI = new DrawUI(this.client);
    }
    
    @Inject(method = "render", at = @At(value = "HEAD", target = "Lnet/minecraft/client/options/GameOptions;hudHidden:Z", ordinal = 2))
    private void onDraw(MatrixStack matrixStack, float f, CallbackInfo ci) {
        if (!this.client.options.debugEnabled) {
            this.drawUI.draw();
        }
    }

    @Inject(method = "resetDebugHudChunk", at = @At(value = "RETURN"))
    private void onReset(CallbackInfo ci) {
    }

    @Inject(at = @At("HEAD"), method = "renderStatusEffectOverlay", cancellable = true)
    private void renderStatusEffectOverlay(CallbackInfo ci) {
        if (Meteorite.config().effects) {
            ci.cancel();
        }
    }
}
