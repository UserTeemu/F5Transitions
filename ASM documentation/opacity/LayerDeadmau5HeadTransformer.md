# LayerDeadmau5Head
```java
public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
{
    if (entitylivingbaseIn.getName().equals("deadmau5") && entitylivingbaseIn.hasSkin() && !entitylivingbaseIn.isInvisible())
    {
+       boolean isEntityRenderEntity;
+       if (isEntityRenderEntity = GeneralEntityRenderingHook.canApplyTransitionsToEntity(entitylivingbaseIn)) {
+           GlStateManager.pushMatrix();
+           GlStateManager.color(1.0F, 1.0F, 1.0F, Minecraft.getMinecraft().entityRenderer.perspectiveTransitionHelper.getArmorOpacity());
+           if (Minecraft.getMinecraft().entityRenderer.perspectiveTransitionHelper.shouldDisableDepthMask()) {
+               GlStateManager.depthMask(false);
+           }
+           GlStateManager.enableBlend();
+           GlStateManager.alphaFunc(516, 0.003921569F);
+       }

        this.playerRenderer.bindTexture(entitylivingbaseIn.getLocationSkin());

        for (int i = 0; i < 2; ++i)
        {
            //...
        }

+       if (isEntityRenderEntity) {
+           GlStateManager.alphaFunc(516, 0.1F);
+           GlStateManager.disableBlend();
+           GlStateManager.depthMask(true);
+           GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
+           GlStateManager.popMatrix();
+       }
    }
}```