package top.katton.mixin;

import com.mojang.renderpearl.api.pipeline.ShaderType;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.katton.client.ClientPostEffectManager;

@Mixin(ShaderManager.Configs.class)
public abstract class ShaderManagerConfigsMixin {
    @Inject(method = "getShader", at = @At("HEAD"), cancellable = true)
    private void katton$getRuntimeShader(Identifier id, ShaderType type, CallbackInfoReturnable<String> cir) {
        String source = ClientPostEffectManager.getRuntimeShaderSource(id, type);
        if (source != null) {
            cir.setReturnValue(source);
        }
    }
}
