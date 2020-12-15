# What does `io.github.tivj.f5transitions.asm.modifications.EntityRendererTransformer` do?
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
                GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, -1.0F, 0.0F, 0.0F);
            }
        }
-       else if (this.mc.gameSettings.thirdPersonView > 0) {
+       else if (this.mc.gameSettings.thirdPersonView > 0 || this.perspectiveTransitionHelper.isTransitionActive()) {
-           double d3 = (double)(this.thirdPersonDistanceTemp + (this.thirdPersonDistance - this.thirdPersonDistanceTemp) * partialTicks);
+           double d3 = (double)(this.perspectiveTransitionHelper.getDistanceMultiplier(partialTicks) * this.thirdPersonDistance);

            if (this.mc.gameSettings.debugCamEnable)
```
...
```java
                 float f2 = entity.rotationPitch;
 
-                if (this.mc.gameSettings.thirdPersonView == 2) {
-                    f2 += 180.0F;
-                }

+               double closestHitDistance = d3 + (d3 > 0D ? 1D : -1D);
-               double d4 = (double)(-MathHelper.sin(f1 / 180.0F * (float)Math.PI) * MathHelper.cos(f2 / 180.0F * (float)Math.PI)) * d3;
-               double d5 = (double)(MathHelper.cos(f1 / 180.0F * (float)Math.PI) * MathHelper.cos(f2 / 180.0F * (float)Math.PI)) * d3;
-               double d6 = (double)(-MathHelper.sin(f2 / 180.0F * (float)Math.PI)) * d3;
+               double d4 = (double)(-MathHelper.sin(f1 / 180.0F * (float)Math.PI) * MathHelper.cos(f2 / 180.0F * (float)Math.PI)) * closestHitDistance;
+               double d5 = (double)(MathHelper.cos(f1 / 180.0F * (float)Math.PI) * MathHelper.cos(f2 / 180.0F * (float)Math.PI)) * closestHitDistance;
+               double d6 = (double)(-MathHelper.sin(f2 / 180.0F * (float)Math.PI)) * closestHitDistance;
+               

```
...
```java
+                       if (d7 < 4D) {
                            if (d7 < d3) {
                                d3 = d7;
+                           } else if (d3 < 0D && -d7 > d3) {
+                               d3 = -d7;
                            }
+                       }
+
+                       if (d7 < closestHitDistance) {
+                           closestHitDistance = d7;
+                           side = movingobjectposition.sideHit;
+                       } else if (closestHitDistance < 0D && -d7 > closestHitDistance) {
+                           closestHitDistance = -d7;
+                           side = movingobjectposition.sideHit;
+                       }
                    }
                }

-               if (this.mc.gameSettings.thirdPersonView == 2) {
-                   GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
-               }
+               GlStateManager.rotate(this.perspectiveTransitionHelper.getYRotationBonus(partialTicks), 0.0F, 1.0F, 0.0F);

                GlStateManager.rotate(entity.rotationPitch - f2, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(entity.rotationYaw - f1, 0.0F, 1.0F, 0.0F);
                GlStateManager.translate(0.0F, 0.0F, (float)(-d3));
+               if (side != null) GlStateManager.translate(0F, 0F, 0.03F);
                GlStateManager.rotate(f1 - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(f2 - entity.rotationPitch, 1.0F, 0.0F, 0.0F);
```
...
```java
        GlStateManager.translate(0.0F, -f, 0.0F);
        d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double)partialTicks;
        d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double)partialTicks + (double)f;
        d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double)partialTicks;
        this.cloudFog = this.mc.renderGlobal.hasCloudFog(d0, d1, d2, partialTicks);
+       if (side != null) GlStateManager.translate(-side.getDirectionVec().getX() * 0.06F, -side.getDirectionVec().getY() * 0.06F, -side.getDirectionVec().getZ() * 0.06F);
        }
```
...