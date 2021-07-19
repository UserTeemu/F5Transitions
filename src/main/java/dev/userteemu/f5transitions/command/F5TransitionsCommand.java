package dev.userteemu.f5transitions.command;

import dev.userteemu.f5transitions.asm.modifications.EntityRendererTransformer;
import dev.userteemu.f5transitions.config.TransitionsConfig;
import dev.userteemu.f5transitions.TransitionHelper;
import dev.userteemu.f5transitions.perspectives.Perspective;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import gg.essential.api.EssentialAPI;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import gg.essential.api.commands.SubCommand;

import java.lang.reflect.Field;

public class F5TransitionsCommand extends Command {
    public F5TransitionsCommand() {
        super("f5transitions");
    }

    @DefaultHandler
    public void handle() {
        EssentialAPI.getGuiUtil().openScreen(TransitionsConfig.INSTANCE.gui());
    }

    @SubCommand("setperspective")
    public void setPerspective(int mode, boolean setMinecraftPerspective) {
        Perspective perspective = TransitionHelper.getPerspectiveFromID(mode);

        if (setMinecraftPerspective) {
            Minecraft.getMinecraft().gameSettings.thirdPersonView = mode;
        }

        try {
            Field field = EntityRenderer.class.getField(EntityRendererTransformer.transitionHelper.name);
            TransitionHelper transitionHelper = (TransitionHelper) field.get(Minecraft.getMinecraft().entityRenderer);
            transitionHelper.changePerspective(perspective, true);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
