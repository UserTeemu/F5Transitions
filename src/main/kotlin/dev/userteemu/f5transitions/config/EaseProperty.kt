package dev.userteemu.f5transitions.config

import gg.essential.elementa.constraints.animation.Animations
import gg.essential.vigilance.Vigilant
import dev.userteemu.f5transitions.utils.readableName

enum class EaseProperty(private val nameOfProperty: String) {
    ROTATION("camera rotation"),
    DISTANCE("camera distance from player"),
    OPACITY("player opacity");

    /**
     * Used as the field for the Vigilance property.
     * This field contains the the selected easing method's ordinal.
     */
    private var selectedOrdinal: Int = Animations.IN_OUT_QUAD.ordinal

    /**
     * Contains the selected easing method.
     * This field is updated whenever the selection changes.
     */
    var animationEaseValue = Animations.IN_OUT_QUAD

    fun getValue(input: Float) = animationEaseValue.getValue(input)

    fun asVigilanceSelector(categoryPropertyBuilder: Vigilant.CategoryPropertyBuilder) {
        return categoryPropertyBuilder.selector(
            ::selectedOrdinal,
            "Animation easing method for $nameOfProperty",
            "What each easing does can be seen at https://easings.net",
            options = animationEasingNames
        ) {
            animationEaseValue = Animations.values()[it]
        }
    }

    companion object {
        /**
         * Takes all animations in Elementa's Animations enum, creates a better name for it and puts them to the array.
         * For example "IN_OUT_QUART" becomes "In Out Quart"
         */
        val animationEasingNames = Animations.values().map { it.readableName }
    }
}