package dev.userteemu.f5transitions.config

import gg.essential.vigilance.Vigilant
import dev.userteemu.f5transitions.compatibility.CompatibilityHelper

object PerspectiveModCompatibilityOptions {
    var rotateShortestDirection = true
    var removeFullTurns = true

    fun addOptions(categoryPropertyBuilder: Vigilant.CategoryPropertyBuilder) = with(categoryPropertyBuilder) {
        subcategory("Perspective Mod") {
            switch(
                PerspectiveModCompatibilityOptions::rotateShortestDirection,
                "Rotate towards shortest direction",
                "When exiting perspective mod state choose the shortest rotation path.",
                hidden = !CompatibilityHelper.isPerspectiveModLoaded
            )
            switch(
                PerspectiveModCompatibilityOptions::removeFullTurns,
                "Remove full turns",
                "Rotations are not more than 360 degrees when enabled.",
                hidden = !CompatibilityHelper.isPerspectiveModLoaded
            )
        }
    }
}