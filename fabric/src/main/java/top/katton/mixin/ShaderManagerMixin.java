package top.katton.mixin;

import com.mojang.renderpearl.api.pipeline.ShaderType;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.Projection;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.katton.client.ClientPostEffectManager;

import java.util.Set;

@Mixin(ShaderManager.class)
public abstract class ShaderManagerMixin {
    @Shadow
    @Final
    private TextureManager textureManager;

    @Shadow
    @Final
    private Projection postChainProjection;

    @Shadow
    @Final
    private ProjectionMatrixBuffer postChainProjectionMatrixBuffer;

    @Inject(method = "getPostChain", at = @At("HEAD"), cancellable = true)
    private void katton$getRuntimePostChain(Identifier id, Set<Identifier> allowedTargets, CallbackInfoReturnable<PostChain> cir) {
        PostChain postChain = ClientPostEffectManager.getOrCreatePostChain(
                id,
                allowedTargets,
                this.textureManager,
                this.postChainProjection,
                this.postChainProjectionMatrixBuffer
        );
        if (postChain != null) {
            cir.setReturnValue(postChain);
        }
    }

    @Inject(
        method = "apply(Lcom/mojang/renderpearl/api/device/GpuDevice;Lnet/minecraft/client/renderer/ShaderManager$PendingResults;)V",
        at = @At("HEAD")
    )
    private void katton$invalidateRuntimePostChains(GpuDevice device, Object compilations, CallbackInfo ci) {
        ClientPostEffectManager.invalidatePostChainCache();
        top.katton.client.scene.ClientSceneManager.resourceReload();
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void katton$closeRuntimePostChains(CallbackInfo ci) {
        ClientPostEffectManager.invalidatePostChainCache();
        top.katton.client.scene.ClientSceneManager.resourceReload();
    }
}
