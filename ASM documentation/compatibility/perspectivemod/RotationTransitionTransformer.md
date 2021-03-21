# RotationTransitionTransformer
This transformer is used to make F5 Transitions compatible with [DJTheRedstoner's PerspectiveModv4](https://github.com/DJtheRedstoner/PerspectiveModv4).

This transformer allows F5 Transitions to change Perspective Mod's camera rotation values in its hooks

Old: 
```return PerspectiveMod.instance.perspectiveToggled ? PerspectiveMod.instance.cameraYaw : entity.rotationYaw;```

New: 
```return PerspectiveMod.instance.perspectiveToggled ? Minecraft.getMinecraft().entityRenderer.perspectiveTransitionHelper.getMultipliedFacingValueForPerspectiveMod(PerspectiveMod.instance.cameraYaw, entity, /*if the field is yaw, else pitch*/, /*is previous value, else current value*/) : entity.rotationYaw;```

Fields that are changed:
- cameraYaw / rotationYaw / prevRotationYaw
- cameraPitch / rotationPitch / prevRotationPitch