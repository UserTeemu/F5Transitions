# ItemRenderer
In `renderItemInFirstPerson`:  
...
```java
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
+       GlStateManager.rotate(-this.mc.entityRenderer.perspectiveTransitionHelper.getYRotationBonus(partialTicks), 0.0F, 1.0F, 0.0F);
        if (this.itemToRender != null) {
            if (this.itemToRender.getItem() instanceof ItemMap) {
```
...