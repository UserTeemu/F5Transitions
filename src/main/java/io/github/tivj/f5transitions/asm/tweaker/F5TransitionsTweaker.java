package io.github.tivj.f5transitions.asm.tweaker;

import io.github.tivj.f5transitions.asm.tweaker.F5TransitionsFMLLoadingPlugin;
import net.modcore.loader.ModCoreSetupTweaker;

public class F5TransitionsTweaker extends ModCoreSetupTweaker {
    public F5TransitionsTweaker() {
        super(new String[]{F5TransitionsFMLLoadingPlugin.class.getName()});
    }
}
