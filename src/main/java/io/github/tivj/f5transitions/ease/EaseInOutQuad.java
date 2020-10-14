package io.github.tivj.f5transitions.ease;

/**
 * taken from https://easings.net/#easeInOutQuad
 */
public class EaseInOutQuad implements IEase {
    @Override
    public float ease(float input) {
        return input < 0.5F ? 2 * input * input : (float) (1 - Math.pow(-2 * input + 2, 2) / 2);
    }
}
