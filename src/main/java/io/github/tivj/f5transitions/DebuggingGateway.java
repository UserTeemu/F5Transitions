package io.github.tivj.f5transitions;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.client.ClientCommandHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DebuggingGateway {
    public static TransitionHelper transition;

    public static Collection<String> debugString() {
        List<String> lines = new ArrayList<>();
        if (transition != null) {
            lines.add("Transition ongoing: " + transition.transitionActive);
            lines.add("Multiplier: " + transition.getDistanceMultiplier(0F));
            lines.add("Y rotation: " + transition.getYRotationBonus(0F));
            lines.add("from -> to perspective: " + (transition.from == null ? "null" : transition.from.getID()) + " -> " + transition.to.getID());
        }
        lines.add("Perspective: " + Minecraft.getMinecraft().gameSettings.thirdPersonView);
        return lines;
    }

    public static void registerCommand() {
        ClientCommandHandler.instance.registerCommand(new DebugCommand());
    }

    public static class DebugCommand extends CommandBase {
        public static float value = 0F;

        @Override
        public String getCommandName() {
            return "f5debug";
        }

        @Override
        public String getCommandUsage(ICommandSender sender) {
            return "/f5debug <pog>";
        }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            if (args.length > 1 && "set".equals(args[0])) value = Float.parseFloat(args[1]);
            else if (args.length > 0) value += Float.parseFloat(args[0]);
            else value++;
        }

        @Override
        public int getRequiredPermissionLevel() {
            return -1;
        }
    }
}