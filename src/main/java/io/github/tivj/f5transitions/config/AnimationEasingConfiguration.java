package io.github.tivj.f5transitions.config;

import club.sk1er.elementa.constraints.animation.Animations;
import club.sk1er.vigilance.data.FieldBackedPropertyValue;
import club.sk1er.vigilance.data.PropertyType;
import club.sk1er.vigilance.data.PropertyValue;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnimationEasingConfiguration {
    public static String[] animationEasingNames;

    static {
        // takes all values of Animations and puts their names to animationEasingNames array.
        // For example "IN_OUT_QUART" becomes "In Out Quart"
        animationEasingNames = new String[Animations.values().length];
        for (int i = 0; i < Animations.values().length; i++) {
            String out = Animations.values()[i].name().replace("_", " ").toLowerCase();
            out = Character.toUpperCase(out.charAt(0)) + out.substring(1);
            Matcher matcher = Pattern.compile("(?<space>( .))").matcher(out);
            while (matcher.find()) {
                out = out.substring(0, matcher.start()) + matcher.group("space").toUpperCase() + out.substring(matcher.start() + 2);
            }

            animationEasingNames[i] = out;
        }
    }

    public enum EaseUse {
        ROTATION("camera rotation"),
        DISTANCE("camera distance from player"),
        OPACITY("player opacity");

        public final String nameOfUse;
        public final Function1<Object, Unit> function;
        public Animations animationEaseValue = Animations.IN_OUT_QUAD;

        EaseUse(String nameOfUse) {
            this.nameOfUse = nameOfUse;
            this.function = o -> setValue((int) o);
        }

        public Unit setValue(int easeIndex) {
            this.animationEaseValue = Animations.values()[easeIndex];
            return null;
        }

        public float getValue(float in) {
            return this.animationEaseValue.getValue(in);
        }
    }

    public static void setupAnimationEasingProperties(TransitionsConfig transitionsConfig) {
        for (EaseUse use : EaseUse.values()) {
            PropertyValue easingMethodIndexField;
            try {
                easingMethodIndexField = new FieldBackedPropertyValue(transitionsConfig.getClass().getField(use.name().toLowerCase() + "EasingMethodIndex"));
            } catch (NoSuchFieldException e) {
                System.out.println("F5 Transitions: Error while creating easing properties:");
                e.printStackTrace();
                return;
            }

            transitionsConfig.category("Animation", (categoryBuilder) -> {
                categoryBuilder.subcategory("Animation", (builder) -> {
                    builder.property(
                            easingMethodIndexField,
                            PropertyType.SELECTOR,
                            "Animation easing method for " + use.nameOfUse,
                            "What each easing does can be seen at https://easings.net/",
                            0,
                            0,
                            0F,
                            0F,
                            1,
                            Arrays.asList(animationEasingNames),
                            true,
                            "",
                            true,
                            false,
                            use.function
                    );
                    return null;
                });
                return null;
            });
        }
    }
}
