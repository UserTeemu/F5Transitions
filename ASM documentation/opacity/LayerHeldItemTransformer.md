# LayerHeldItem
```java
public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale)
{
    ItemStack itemstack = entitylivingbaseIn.getHeldItem();

-   if (itemstack != null)
+   if (itemstack != null && (!entitylivingbaseIn.equals(Minecraft.getMinecraft().getRenderViewEntity()) || Minecraft.getMinecraft().entityRenderer.perspectiveTransitionHelper.shouldItemBeRenderedInThirdPerson()))
    {
        GlStateManager.pushMatrix();
```