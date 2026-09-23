package top.katton.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.katton.client.scene.ClientSceneManager;

@Mixin(KeyboardHandler.class)
public abstract class SceneKeyboardMixin {
    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    private void katton$skip(long window, int action, KeyEvent event, CallbackInfo ci) {
        if (event.key() == InputConstants.KEY_ESCAPE && action == InputConstants.PRESS && ClientSceneManager.skipCamera()) ci.cancel();
    }
}
