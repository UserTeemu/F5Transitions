# RendererLivingEntityTransformer
```java
protected void renderModel(T entitylivingbaseIn, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float scaleFactor)
{
    boolean flag = !entitylivingbaseIn.isInvisible();
    boolean flag1 = !flag && !entitylivingbaseIn.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer);

    if (flag || flag1)
    {
        if (!this.bindEntityTexture(entitylivingbaseIn))
        {
            return;
        }

-       if (flag1)
-       if (flag1 || entitylivingbaseIn.equals(Minecraft.getMinecraft().getRenderViewEntity()))
        {
            GlStateManager.pushMatrix();
-           GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
+           GlStateManager.color(1.0F, 1.0F, 1.0F, isPlayer ? Minecraft.getMinecraft().entityRenderer.perspectiveTransitionHelper.getPlayerOpacity() : 0.15F);
+           if (!isPlayer || Minecraft.getMinecraft().entityRenderer.perspectiveTransitionHelper.isPlayerNotRenderedSolid()){
                GlStateManager.depthMask(false);
+           }
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
-           GlStateManager.alphaFunc(516, 0.003921569F);
+           GlStateManager.alphaFunc(516, isPlayer ? Minecraft.getMinecraft().entityRenderer.perspectiveTransitionHelper.getPlayerOpacity() / 10.0F : 0.003921569F);
        }

        this.mainModel.render(entitylivingbaseIn, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, scaleFactor);

-       if (flag1)
-       if (flag1 || entitylivingbaseIn.equals(Minecraft.getMinecraft().getRenderViewEntity()))
        {
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
        }
    }
}
```