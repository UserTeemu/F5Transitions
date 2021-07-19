package dev.userteemu.f5transitions.compatibility;

import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

import java.lang.reflect.Field;

public class CompatibilityHelper {
    public static boolean isPatcherLoaded; // https://sk1er.club/mods/patcher
    public static boolean isPerspectiveModLoaded; // https://github.com/DJtheRedstoner/PerspectiveModv4

    static {
        try {
            Field instanceField = FMLLaunchHandler.class.getDeclaredField("INSTANCE");
            instanceField.setAccessible(true);
            FMLLaunchHandler launchHandler = (FMLLaunchHandler) instanceField.get(null);

            Field classLoaderField = FMLLaunchHandler.class.getDeclaredField("classLoader");
            classLoaderField.setAccessible(true);
            LaunchClassLoader classLoader = (LaunchClassLoader) classLoaderField.get(launchHandler);

            try {
                isPerspectiveModLoaded = classLoader.findClass("me.djtheredstoner.perspectivemod.asm.transformers.MinecraftTransformer") != null;
            } catch (ClassNotFoundException ignored) { }

            try {
                isPatcherLoaded = classLoader.findClass("club.sk1er.patcher.Patcher") != null;
            } catch (ClassNotFoundException ignored) { }
        }
        catch (Exception e) {
            System.out.println("Failed checking for other mods");
        }
    }
}
