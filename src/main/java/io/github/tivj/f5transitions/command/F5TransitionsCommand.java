package io.github.tivj.f5transitions.command;

import io.github.tivj.f5transitions.config.TransitionsConfig;
import net.modcore.api.ModCoreAPI;
import net.modcore.api.commands.Command;
import net.modcore.api.commands.DefaultHandler;

public class F5TransitionsCommand extends Command {
    public F5TransitionsCommand() {
        super("f5transitions");
    }

    @DefaultHandler
    public void handle() {
        ModCoreAPI.getGuiUtil().openScreen(TransitionsConfig.INSTANCE.gui());
    }
}
