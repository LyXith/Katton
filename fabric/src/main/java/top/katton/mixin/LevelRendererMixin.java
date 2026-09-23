package top.katton.mixin;

import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
/*? if mc_26_1_2 {*/
/*import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;*/
/*?}*/
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.katton.api.KattonClientRenderApiKt;
import top.katton.client.ClientItemRenderMarkerManager;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Unique
    private float katton$tickDelta;

    @Inject(method = "render", at = @At("HEAD"))
    private void katton$captureTickDelta(
            GraphicsResourceAllocator resourceAllocator,
            boolean renderOutline,
            CameraRenderState cameraState,
            GpuBufferSlice terrainFog,
            Vector4f fogColor,
            boolean shouldRenderSky,
            boolean consistentDepthRequired,
            CallbackInfo ci
    ) {
        this.katton$tickDelta = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void katton$renderWorld(
            GraphicsResourceAllocator resourceAllocator,
            boolean renderOutline,
            CameraRenderState cameraState,
            GpuBufferSlice terrainFog,
            Vector4f fogColor,
            boolean shouldRenderSky,
            boolean consistentDepthRequired,
            CallbackInfo ci
    ) {
        KattonClientRenderApiKt.dispatchWorldRender(cameraState, this.katton$tickDelta);
    }

    @Inject(method = "submitEntities", at = @At("TAIL"))
    private void katton$renderItemMarkers(
            PoseStack poseStack,
            LevelRenderState levelRenderState,
            SubmitNodeCollector submitNodeCollector,
            CallbackInfo ci
    ) {
        ClientItemRenderMarkerManager.render(levelRenderState.cameraRenderState, poseStack, submitNodeCollector, this.katton$tickDelta);
    }
}
