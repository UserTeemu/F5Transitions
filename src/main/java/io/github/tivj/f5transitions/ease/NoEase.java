package io.github.tivj.f5transitions.ease;

public class NoEase implements IEase {
    @Override
    public float ease(float input) {
        return input;
    }
}
