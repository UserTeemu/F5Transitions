# LayerArmorBase
```java
    private void renderLayer(EntityLivingBase entitylivingbaseIn, float p_177182_2_, float p_177182_3_, float partialTicks, float p_177182_5_, float p_177182_6_, float p_177182_7_, float scale, int armorSlot)
    {
        ItemStack itemstack = this.getCurrentArmor(entitylivingbaseIn, armorSlot);

        if (itemstack != null && itemstack.getItem() instanceof ItemArmor)
        {
            ItemArmor itemarmor = (ItemArmor)itemstack.getItem();
            T t = this.getArmorModel(armorSlot);
            t.setModelAttributes(this.renderer.getMainModel());
            t.setLivingAnimations(entitylivingbaseIn, p_177182_2_, p_177182_3_, partialTicks);
            t = getArmorModelHook(entitylivingbaseIn, itemstack, armorSlot, t);
            this.setModelPartVisible(t, armorSlot);
            boolean flag = this.isSlotForLeggings(armorSlot);
            this.renderer.bindTexture(this.getArmorResource(entitylivingbaseIn, itemstack, flag ? 2 : 1, null));

+           boolean isEntityRenderEntity = entitylivingbaseIn.equals(Minecraft.getMinecraft().getRenderViewEntity());
+
+           if (isEntityRenderEntity) {
+               GlStateManager.pushMatrix();
+               if (Minecraft.getMinecraft().entityRenderer.perspectiveTransitionHelper.shouldDisableDepthMask()) GlStateManager.depthMask(false);
+               GlStateManager.enableBlend();
+               GlStateManager.alphaFunc(516, 0.003921569F);
+           }

            int i = itemarmor.getColor(itemstack);
            if (i != -1) { // Allow this for anything, not only cloth.
                float f = (float)(i >> 16 & 255) / 255.0F;
                float f1 = (float)(i >> 8 & 255) / 255.0F;
                float f2 = (float)(i & 255) / 255.0F;
-               GlStateManager.color(this.colorR * f, this.colorG * f1, this.colorB * f2, this.alpha);
+               GlStateManager.color(this.colorR * f, this.colorG * f1, this.colorB * f2, isEntityRenderEntity ? Minecraft.getMinecraft().entityRenderer.perspectiveTransitionHelper.getArmorOpacity() : this.alpha);
                t.render(entitylivingbaseIn, p_177182_2_, p_177182_3_, p_177182_5_, p_177182_6_, p_177182_7_, scale);
                this.renderer.bindTexture(this.getArmorResource(entitylivingbaseIn, itemstack, flag ? 2 : 1, "overlay"));
            }
            
            // Non-colored
-           GlStateManager.color(this.colorR, this.colorG, this.colorB, this.alpha);
+           GlStateManager.color(this.colorR, this.colorG, this.colorB, isEntityRenderEntity ? Minecraft.getMinecraft().entityRenderer.perspectiveTransitionHelper.getArmorOpacity() : this.alpha);
            t.render(entitylivingbaseIn, p_177182_2_, p_177182_3_, p_177182_5_, p_177182_6_, p_177182_7_, scale);
            
            // Default
            if (!this.skipRenderGlint && itemstack.hasEffect()) {
                this.renderGlint(entitylivingbaseIn, t, p_177182_2_, p_177182_3_, partialTicks, p_177182_5_, p_177182_6_, p_177182_7_, scale);
            }

+           if (isEntityRenderEntity) {
+               GlStateManager.alphaFunc(516, 0.1F);
+               GlStateManager.disableBlend();
+               GlStateManager.depthMask(true);
+               GlStateManager.popMatrix();
+           }
        }
    }
```