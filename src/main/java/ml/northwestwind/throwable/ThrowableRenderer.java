package ml.northwestwind.throwable;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class ThrowableRenderer extends EntityRenderer<ThrowableItemEntity> {
    private final net.minecraft.client.renderer.ItemRenderer itemRenderer;
    private final Random random = new Random();

    public ThrowableRenderer(EntityRendererManager p_i46167_1_, net.minecraft.client.renderer.ItemRenderer p_i46167_2_) {
        super(p_i46167_1_);
        this.itemRenderer = p_i46167_2_;
        this.shadowRadius = 0.15F;
        this.shadowStrength = 0.75F;
    }

    protected int getRenderAmount(ItemStack p_177078_1_) {
        int i = 1;
        if (p_177078_1_.getCount() > 48) {
            i = 5;
        } else if (p_177078_1_.getCount() > 32) {
            i = 4;
        } else if (p_177078_1_.getCount() > 16) {
            i = 3;
        } else if (p_177078_1_.getCount() > 1) {
            i = 2;
        }

        return i;
    }

    public void render(ThrowableItemEntity entity, float p_225623_2_, float p_225623_3_, MatrixStack matrix, IRenderTypeBuffer buffer, int p_225623_6_) {
        matrix.pushPose();
        ItemStack itemstack = entity.getItem();
        int i = itemstack.isEmpty() ? 187 : Item.getId(itemstack.getItem()) + itemstack.getDamageValue();
        this.random.setSeed(i);
        IBakedModel ibakedmodel = this.itemRenderer.getModel(itemstack, entity.level, null);
        boolean flag = ibakedmodel.isGui3d();
        int j = this.getRenderAmount(itemstack);
        float f2 = shouldBob() ? ibakedmodel.getTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.y() : 0;
        matrix.translate(0.0D, 0.25F * f2, 0.0D);
        if (!flag) {
            float f7 = -0.0F * (float)(j - 1) * 0.5F;
            float f8 = -0.0F * (float)(j - 1) * 0.5F;
            float f9 = -0.09375F * (float)(j - 1) * 0.5F;
            matrix.translate(f7, f8, f9);
        }

        for(int k = 0; k < j; ++k) {
            matrix.pushPose();
            if (k > 0) {
                if (flag) {
                    float f11 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f13 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f10 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    matrix.translate(shouldSpreadItems() ? f11 : 0, shouldSpreadItems() ? f13 : 0, shouldSpreadItems() ? f10 : 0);
                } else {
                    float f12 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    float f14 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    matrix.translate(shouldSpreadItems() ? f12 : 0, shouldSpreadItems() ? f14 : 0, 0.0D);
                }
            }

            this.itemRenderer.render(itemstack, ItemCameraTransforms.TransformType.GROUND, false, matrix, buffer, p_225623_6_, OverlayTexture.NO_OVERLAY, ibakedmodel);
            matrix.popPose();
            if (!flag) {
                matrix.translate(0.0, 0.0, 0.09375F);
            }
        }

        matrix.popPose();
        super.render(entity, p_225623_2_, p_225623_3_, matrix, buffer, p_225623_6_);
    }

    public ResourceLocation getTextureLocation(ThrowableItemEntity p_110775_1_) {
        return AtlasTexture.LOCATION_BLOCKS;
    }

    public boolean shouldSpreadItems() {
        return true;
    }
    public boolean shouldBob() {
        return true;
    }
}
