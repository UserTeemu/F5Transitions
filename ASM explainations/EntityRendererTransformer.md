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
```
...
```java
                        if (d7 < d3) {
                            d3 = d7;
+                       } else if (d3 < 0D && -d7 > d3) {
+                           d3 = -d7;
                        }
                    }
+                   d3 = this.perspectiveTransitionHelper.ensureGoodDistance(distance, movingobjectposition == null ? new Vec3(entityPosX - (facingAtXCoord / distance * unmodifiedDistance), entityPosY - (facingAtZCoord / distance * unmodifiedDistance), entityPosZ - (facingAtYCoord / distance * unmodifiedDistance)) : movingobjectposition.hitVec);
                }

-               if (this.mc.gameSettings.thirdPersonView == 2) {
-                   GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
-               }
+               GlStateManager.rotate(this.perspectiveTransitionHelper.getYRotationBonus(partialTicks), 0.0F, 1.0F, 0.0F);

                GlStateManager.rotate(entity.rotationPitch - f2, 1.0F, 0.0F, 0.0F);
```
...