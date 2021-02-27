# PerspectiveMod
This transformer is used to make F5 Transitions compatible with [DJTheRedstoner's PerspectiveModv4](https://github.com/DJtheRedstoner/PerspectiveModv4).

```java
    public void enterPerspective() {
+       Minecraft.getMinecraft().entityRenderer.perspectiveTransitionHelper.changePerspective(getPerspectiveFromID(1), true);
        perspectiveToggled = true;
        previousPerspective = mc.gameSettings.thirdPersonView;
        mc.gameSettings.thirdPersonView = 1;
    }

    public void resetPerspective() {
+       Minecraft.getMinecraft().entityRenderer.perspectiveTransitionHelper.changePerspective(getPerspectiveFromID(previousPerspective), true);
        perspectiveToggled = false;
        mc.gameSettings.thirdPersonView = previousPerspective;
    }
```

Perspective Mod codebase is as of commit [7a42b12](https://github.com/DJtheRedstoner/PerspectiveModv4/tree/7a42b12aa3bbb389dd1cdc9c1bd2d81b8a4da2bb).