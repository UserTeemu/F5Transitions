# LayerCape
```java
public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
{
    if (entitylivingbaseIn.hasPlayerInfo() && !entitylivingbaseIn.isInvisible() && entitylivingbaseIn.isWearing(EnumPlayerModelParts.CAPE) && entitylivingbaseIn.getLocationCape() != null)
    {
+       boolean isEntityRenderEntity;
+       if (isEntityRenderEntity = entitylivingbaseIn.equals(Minecraft.getMinecraft().getRenderViewEntity())) {
+           if (Minecraft.getMinecraft().entityRenderer.perspectiveTransitionHelper.shouldDisableDepthMask()) {
+               GlStateManager.depthMask(false);
+           }
+
+           GlStateManager.enableBlend();
+           GlStateManager.alphaFunc(516, 0.003921569F);
+       }

-       GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
+       GlStateManager.color(1.0F, 1.0F, 1.0F, isEntityRenderEntity ? Minecraft.getMinecraft().entityRenderer.perspectiveTransitionHelper.getArmorOpacity() : 1.0F);
        this.playerRenderer.bindTexture(entitylivingbaseIn.getLocationCape());
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 0.125F);
        
        //...

        GlStateManager.rotate(6.0F + f2 / 2.0F + f1, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        this.playerRenderer.getMainModel().renderCape(0.0625F);
+       if (isEntityRenderEntity) {
+           GlStateManager.alphaFunc(516, 0.1F);
+           GlStateManager.disableBlend();
+           GlStateManager.depthMask(true)
+       }

        GlStateManager.popMatrix();
    }
}```