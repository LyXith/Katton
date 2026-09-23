package top.katton.mixin;

import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.katton.platform.FabricEntityRendererHooks;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

    /**
     * Intercepts getRenderer(Entity) to check Katton-managed renderers first.
     */
    @SuppressWarnings("unchecked")
    @Inject(method = "getRenderer*", at = @At("HEAD"), cancellable = true)
    private <T extends Entity> void katton$injectEntityRenderer(T entity, CallbackInfoReturnable<EntityRenderer<? super T, ?>> cir) {
        EntityType<?> type = entity.getType();
        EntityRenderer<?, ?> kattonRenderer = FabricEntityRendererHooks.INSTANCE.getKattonRenderer(type);
        if (kattonRenderer != null) {
            cir.setReturnValue((EntityRenderer<? super T, ?>) kattonRenderer);
        }
    }

    /**
     * Intercepts getRenderer(EntityRenderState) — critical for MC 26.1+
     * where submit() uses the state-based lookup, not entity-based.
     */
    @SuppressWarnings("unchecked")
    @Inject(method = "getRenderer*", at = @At("HEAD"), cancellable = true)
    private <S extends EntityRenderState> void katton$injectStateRenderer(S state, CallbackInfoReturnable<EntityRenderer<?, ? super S>> cir) {
        EntityRenderer<?, ?> renderer = FabricEntityRendererHooks.INSTANCE.findKattonRendererByState(state);
        if (renderer != null) {
            cir.setReturnValue((EntityRenderer<?, ? super S>) renderer);
        }
    }

    /**
     * Guards against entities with no renderer at all.
     */
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private <E extends Entity> void katton$guardMissingRenderer(E entity,
            net.minecraft.client.renderer.culling.Frustum frustum,
            double x, double y, double z,
            float partialTicks,
            CallbackInfoReturnable<Boolean> cir) {
        EntityRenderDispatcher self = (EntityRenderDispatcher) (Object) this;
        if (self.getRenderer(entity) == null) {
            cir.setReturnValue(false);
        }
    }
}
