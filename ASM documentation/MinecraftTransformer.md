# Minecraft
```java
    if (this.thePlayer != null && this.thePlayer.isEntityInsideOpaqueBlock()) {
        this.gameSettings.thirdPersonView = 0;
+       this.entityRenderer.perspectiveTransitionHelper.changePerspective(TransitionHelper.getPerspectiveFromID(this.gameSettings.thirdPersonView), true);
    }
```
...
```java
    if (this.gameSettings.keyBindTogglePerspective.isPressed()) {
        this.gameSettings.thirdPersonView++;
        if (this.gameSettings.thirdPersonView > 2) {
            this.gameSettings.thirdPersonView = 0;
        }

        if (this.gameSettings.thirdPersonView == 0) {
            this.entityRenderer.loadEntityShader(this.getRenderViewEntity());
        } else if (this.gameSettings.thirdPersonView == 1) {
            this.entityRenderer.loadEntityShader((Entity)null);
        }

+       this.entityRenderer.perspectiveTransitionHelper.changePerspective(TransitionHelper.getPerspectiveFromID(this.gameSettings.thirdPersonView), true);
        this.renderGlobal.setDisplayListEntitiesDirty();
    }
```
...