# EntityRenderer
```java
    public EntityRenderer(Minecraft mcIn, IResourceManager resourceManagerIn) {
+       this.perspectiveTransitionHelper = new TransitionHelper(this, mcIn);
        this.shaderIndex = shaderCount;
```
...
```java
        this.fogColor2 = this.fogColor1;
-       this.thirdPersonDistanceTemp = this.thirdPersonDistance;
+       this.perspectiveTransitionHelper.updatePerspectiveTimer();

        if (this.mc.gameSettings.smoothCamera)
```
...
```java
    private void orientCamera(float partialTicks) {
+       EnumFacing side = null;
        Entity entity = this.mc.getRenderViewEntity();
```
...
```java
-       else if (this.mc.gameSettings.thirdPersonView > 0) {
+       else if (this.mc.gameSettings.thirdPersonView > 0 || this.perspectiveTransitionHelper.isTransitionActive()) {
-           double d3 = (double)(this.thirdPersonDistanceTemp + (this.thirdPersonDistance - this.thirdPersonDistanceTemp) * partialTicks);
+           double d3 = this.perspectiveTransitionHelper.getCameraDistance(partialTicks);

            if (this.mc.gameSettings.debugCamEnable)
            {
                GlStateManager.translate(0.0F, 0.0F, (float)(-d3));
            }
            else
            {
                float f1 = entity.rotationYaw;
                float f2 = entity.rotationPitch;

-               if (this.mc.gameSettings.thirdPersonView == 2) {
-                   f2 += 180.0F;
-               }

-               double d4 = (double)(-MathHelper.sin(f1 / 180.0F * (float)Math.PI) * MathHelper.cos(f2 / 180.0F * (float)Math.PI)) * d3;
-               double d5 = (double)(MathHelper.cos(f1 / 180.0F * (float)Math.PI) * MathHelper.cos(f2 / 180.0F * (float)Math.PI)) * d3;
-               double d6 = (double)(-MathHelper.sin(f2 / 180.0F * (float)Math.PI)) * d3;
+               double d4 = (double)(-MathHelper.sin(f1 / 180.0F * (float)Math.PI) * MathHelper.cos(f2 / 180.0F * (float)Math.PI)) * (d3 + 0.3D);
+               double d5 = (double)(MathHelper.cos(f1 / 180.0F * (float)Math.PI) * MathHelper.cos(f2 / 180.0F * (float)Math.PI)) * (d3 + 0.3D);
+               double d6 = (double)(-MathHelper.sin(f2 / 180.0F * (float)Math.PI)) * (d3 + 0.3D);

                for (int i = 0; i < 8; ++i)
                {
-                   float f3 = (float)((i & 1) * 2 - 1);
-                   float f4 = (float)((i >> 1 & 1) * 2 - 1);
-                   float f5 = (float)((i >> 2 & 1) * 2 - 1);
-                   f3 = f3 * 0.1F;
-                   f4 = f4 * 0.1F;
-                   f5 = f5 * 0.1F;
-                   MovingObjectPosition movingobjectposition = this.mc.theWorld.rayTraceBlocks(new Vec3(d0 + (double)f3, d1 + (double)f4, d2 + (double)f5), new Vec3(d0 - d4 + (double)f3 + (double)f5, d1 - d6 + (double)f4, d2 - d5 + (double)f5));

+                   float f3 = (float)((i & 1) * 2 - 1) * 0.1F;
+                   float f4 = (float)((i >> 1 & 1) * 2 - 1) * 0.1F;
+                   float f5 = (float)((i >> 2 & 1) * 2 - 1) * 0.1F;
+                   MovingObjectPosition movingobjectposition = RaytracingUtil.rayTraceBlocks(this.mc.theWorld, new Vec3(d0 + (double)f3, d1 + (double)f4, d2 + (double)f5), new Vec3(d0 - d4 + (double)f3 + (double)f5, d1 - d6 + (double)f4, d2 - d5 + (double)f5));

                    if (movingobjectposition != null)
                    {
                        double d7 = movingobjectposition.hitVec.distanceTo(new Vec3(d0, d1, d2));
                        
                        if (d7 < d3) {
                            d3 = d7;
+                       } else if (d3 < 0D && -d7 > d3) {
+                           d3 = -d7;
                        }
                    }
                }

-               if (this.mc.gameSettings.thirdPersonView == 2) {
-                   GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
-               }
+               GlStateManager.rotate(this.perspectiveTransitionHelper.getYRotationBonus(partialTicks), 0.0F, 1.0F, 0.0F);

                GlStateManager.rotate(entity.rotationPitch - f2, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(entity.rotationYaw - f1, 0.0F, 1.0F, 0.0F);
                GlStateManager.translate(0.0F, 0.0F, (float)(-d3));
+               GlStateManager.translate(0.0F, 0.0F, 0.01F);
                GlStateManager.rotate(f1 - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(f2 - entity.rotationPitch, 1.0F, 0.0F, 0.0F);
            }
        }
```
...