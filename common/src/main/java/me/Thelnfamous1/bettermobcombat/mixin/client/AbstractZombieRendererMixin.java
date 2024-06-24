package me.Thelnfamous1.bettermobcombat.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.core.util.Vec3f;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.AbstractZombieRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.world.entity.monster.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AbstractZombieRenderer.class)
public abstract class AbstractZombieRendererMixin <
        T extends Zombie,
        M extends ZombieModel<T>
        >
        extends HumanoidMobRenderer<T, M> {

    public AbstractZombieRendererMixin(EntityRendererProvider.Context $$0, M $$1, float $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    public void render(T zombie, float $$1, float $$2, PoseStack $$3, MultiBufferSource $$4, int $$5) {
        if (FirstPersonMode.isFirstPersonPass()) {
            var animationApplier = ((IAnimatedPlayer) zombie).playerAnimator_getAnimation();
            var config = animationApplier.getFirstPersonConfiguration();

            if (zombie == Minecraft.getInstance().getCameraEntity()) {
                // Hiding all parts, because they should not be visible in first person
                setAllPartsVisible(false);
                // Showing arms based on configuration
                var showRightArm = config.isShowRightArm();
                var showLeftArm = config.isShowLeftArm();
                this.model.rightArm.visible = showRightArm;
                this.model.leftArm.visible = showLeftArm;
            }
        }

        // No `else` case needed to show parts, since the default state should be correct already
        super.render(zombie, $$1, $$2, $$3, $$4, $$5);
    }

    @Unique
    private void setAllPartsVisible(boolean visible) {
        this.model.head.visible = visible;
        this.model.body.visible = visible;
        this.model.leftLeg.visible = visible;
        this.model.rightLeg.visible = visible;
        this.model.rightArm.visible = visible;
        this.model.leftArm.visible = visible;

        this.model.hat.visible = visible;
    }

    @Override
    protected void setupRotations(T zombie, PoseStack matrixStack, float $$2, float $$3, float tickDelta) {
        super.setupRotations(zombie, matrixStack, $$2, $$3, tickDelta);
        var animationPlayer = ((IAnimatedPlayer) zombie).playerAnimator_getAnimation();
        animationPlayer.setTickDelta(tickDelta);
        if(animationPlayer.isActive()){

            //These are additive properties
            Vec3f vec3d = animationPlayer.get3DTransform("body", TransformType.POSITION, Vec3f.ZERO);
            matrixStack.translate(vec3d.getX(), vec3d.getY() + 0.7, vec3d.getZ());
            Vec3f vec3f = animationPlayer.get3DTransform("body", TransformType.ROTATION, Vec3f.ZERO);
            matrixStack.mulPose(Axis.ZP.rotation(vec3f.getZ()));    //roll
            matrixStack.mulPose(Axis.YP.rotation(vec3f.getY()));    //pitch
            matrixStack.mulPose(Axis.XP.rotation(vec3f.getX()));    //yaw
            matrixStack.translate(0, - 0.7d, 0);
        }
    }
}
