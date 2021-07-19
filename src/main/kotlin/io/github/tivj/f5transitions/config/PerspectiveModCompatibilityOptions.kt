package io.github.tivj.f5transitions.config

import gg.essential.vigilance.Vigilant
import io.github.tivj.f5transitions.compatibility.CompatibilityHelper

object PerspectiveModCompatibilityOptions {
    var rotateShortestDirection = true
    var removeFullTurns = true

    fun addOptions(categoryPropertyBuilder: Vigilant.CategoryPropertyBuilder) = with(categoryPropertyBuilder) {
        subcategory("Perspective Mod") {
            switch(
                ::rotateShortestDirection,
                "Rotate towards shortest direction",
                "When exiting perspective mod state choose the shortest rotation path.",
                hidden = !CompatibilityHelper.isPerspectiveModLoaded
            )
            switch(
                ::removeFullTurns,
                "Remove full turns",
                "Rotations are not more than 360 degrees when enabled.",
                hidden = !CompatibilityHelper.isPerspectiveModLoaded
            )
        }
    }
}