package dev.userteemu.f5transitions.config

import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import java.io.File

object TransitionsConfig : Vigilant(File("./config/f5transitions.toml")) {

    @Property(
        type = PropertyType.DECIMAL_SLIDER,
        name = "Animation length",
        description = "in client ticks",
        maxF = 100F,
        decimalPlaces = 3,
        category = "Animation",
        subcategory = "Animation"
    )
    var maxPerpectiveTimer = 16F

    @Property(
        type = PropertyType.SWITCH,
        name = "Rotate camera clockwise",
        category = "Animation",
        subcategory = "Animation"
    )
    var rotateCameraClockwise = false

    @Property(
        type = PropertyType.SWITCH,
        name = "Camera always rotates to the same direction",
        category = "Animation",
        subcategory = "Animation"
    )
    var sameCameraRotationDirection = true

    @Property(
        type = PropertyType.PERCENT_SLIDER,
        name = "Player solidness point",
        description = "The lowest player opacity value when the player will be rendered solid",
        category = "Animation",
        subcategory = "Animation details - Probably not wise to change unless you need to"
    )
    var playerSolidnessPoint = 0.6F

    @Property(
        type = PropertyType.PERCENT_SLIDER,
        name = "Item invisibility point",
        description = "When going to 1st person view, the highest player opacity value when the item player is holding can be rendered",
        category = "Animation",
        subcategory = "Animation details - Probably not wise to change unless you need to"
    )
    var thirdPersonItemHidePoint = 0.4F

    @Property(
        type = PropertyType.PERCENT_SLIDER,
        name = "Arrows on player invisibility point",
        description = "When going to 1st person view, the lowest player opacity value when arrows on the player can be seen",
        category = "Animation",
        subcategory = "Animation details - Probably not wise to change unless you need to"
    )
    var arrowLayerHidePoint = 0.18F

    init {
        category("Animation") {
            subcategory("Animation") {
                EaseProperty.values().forEach {
                    it.asVigilanceSelector(this)
                }
            }
            PerspectiveModCompatibilityOptions.addOptions(this)
        }

        category("Collisions") {
            subcategory("Master Toggle") {
                switch(
                    CameraCollisionException.Companion::changeCollidableBlocks,
                    "Change blocks that camera can collide with"
                )
            }
            subcategory("Camera can go through...") {
                CameraCollisionException.values().forEach {
                    it.asVigilanceSwitch(this)
                }
            }
        }

        initialize()
    }
}