# PerspectiveMod
This transformer is used to make F5 Transitions compatible with [DJTheRedstoner's PerspectiveModv4](https://github.com/DJtheRedstoner/PerspectiveModv4).

```java
+   public boolean exitTransitionActive = false;

    public void onPressed(boolean state) {
        if (config.modEnabled) {
            if (state) {
-               cameraYaw = mc.thePlayer.rotationYaw;
-               cameraPitch = mc.thePlayer.rotationPitch;
        
                if (perspectiveToggled) {
                    resetPerspective();
                } else {
                    enterPerspective();
                }
            } else if (config.holdMode) {
                resetPerspective();
            }
        } else if (perspectiveToggled) {
            resetPerspective();
        }
    }

    public void enterPerspective() {
+       if (!this.exitTransitionActive) {
+           this.cameraYaw = this.mc.thePlayer.rotationYaw;
+           this.cameraPitch = this.mc.thePlayer.rotationPitch;
+           this.mc.entityRenderer.perspectiveTransitionHelper.changePerspective(TransitionHelper.getPerspectiveFromID(3), true, true);
            this.perspectiveToggled = true;
            this.previousPerspective = this.mc.gameSettings.thirdPersonView;
            this.mc.gameSettings.thirdPersonView = 1;
+       }
    }

    public void resetPerspective() {
+       if (!this.exitTransitionActive) {
+           this.mc.entityRenderer.perspectiveTransitionHelper.changePerspective(TransitionHelper.getPerspectiveFromID(this.previousPerspective), true, true);
+           this.mc.entityRenderer.perspectiveTransitionHelper.setTransitionFinishCallback(() -> {
                this.perspectiveToggled = false;
+               this.exitTransitionActive = false;
+               this.cameraYaw = this.mc.thePlayer.rotationYaw;
+               this.cameraPitch = this.mc.thePlayer.rotationPitch;
+           });
+       }
        this.mc.gameSettings.thirdPersonView = this.previousPerspective;
    }
```

Perspective Mod codebase is as of commit [7a42b12](https://github.com/DJtheRedstoner/PerspectiveModv4/tree/7a42b12aa3bbb389dd1cdc9c1bd2d81b8a4da2bb).