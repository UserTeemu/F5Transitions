# RenderGlobal
```java
public void renderEntities(Entity renderViewEntity, ICamera camera, float partialTicks)
{
    int pass = net.minecraftforge.client.MinecraftForgeClient.getRenderPass();
    if (this.renderEntitiesStartupCounter > 0)
    {
        if (pass > 0) return;
        --this.renderEntitiesStartupCounter;
    }
    else
    {
        double d0 = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * (double)partialTicks;
        double d1 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * (double)partialTicks;
        double d2 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * (double)partialTicks;
        this.theWorld.theProfiler.startSection("prepare");
+       GeneralEntityRenderingHook.isRightRenderContextForTransitions = true;
```
...
```java
        boolean flag3 = this.mc.getRenderViewEntity() instanceof EntityLivingBase ? ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping() : false;
    
-       if ((entity2 != this.mc.getRenderViewEntity() || this.mc.gameSettings.thirdPersonView != 0 || flag3) && (entity2.posY < 0.0D || entity2.posY >= 256.0D || this.theWorld.isBlockLoaded(new BlockPos(entity2))))
+       if ((entity2 != this.mc.getRenderViewEntity() || this.mc.gameSettings.thirdPersonView != 0 || this.mc.entityRenderer.perspectiveTransitionHelper.isTransitionActive() || flag3) && (entity2.posY < 0.0D || entity2.posY >= 256.0D || this.theWorld.isBlockLoaded(new BlockPos(entity2))))
        {
            ++this.countEntitiesRendered;
            this.renderManager.renderEntitySimple(entity2, partialTicks);
            break;
        }
    }
```
...
```java
+       GeneralEntityRenderingHook.isRightRenderContextForTransitions = false;
    } // end of the renderEntities method
```
