# LayerCustomHead
```java
public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
{
    ItemStack itemstack = entitylivingbaseIn.getCurrentArmor(3);

    if (itemstack != null && itemstack.getItem() != null)
    {
        Item item = itemstack.getItem();
        Minecraft minecraft = Minecraft.getMinecraft();
        GlStateManager.pushMatrix();

+       boolean isEntityRenderEntity = entitylivingbaseIn.equals(Minecraft.getMinecraft().getRenderViewEntity());
+
+       if (isEntityRenderEntity) {
+           if (Minecraft.getMinecraft().entityRenderer.perspectiveTransitionHelper.isPlayerDepthMaskFalse()) GlStateManager.depthMask(false);
+           GlStateManager.enableBlend();
+           GlStateManager.alphaFunc(516, 0.003921569F);
+       }
```
...
```
            this.field_177209_a.postRender(0.0625F);
-           GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
+           GlStateManager.color(1.0F, 1.0F, 1.0F, isEntityRenderEntity ? Minecraft.getMinecraft().entityRenderer.perspectiveTransitionHelper.getArmorOpacity() : 1.0F);
```
...
```
            TileEntitySkullRenderer.instance.renderSkull(-0.5F, 0.0F, -0.5F, EnumFacing.UP, 180.0F, itemstack.getMetadata(), gameprofile, -1);
        }

+       if (isEntityRenderEntity) {
+           GlStateManager.alphaFunc(516, 0.1F);
+           GlStateManager.disableBlend();
+           GlStateManager.depthMask(true);
+       }
        GlStateManager.popMatrix();
    }
}
```