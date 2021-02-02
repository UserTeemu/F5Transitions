package io.github.tivj.f5transitions;

import io.github.tivj.f5transitions.command.F5TransitionsCommand;
import io.github.tivj.f5transitions.config.TransitionsConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.modcore.api.ModCoreAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.awt.image.ToolkitImage;

@Mod(modid = TransitionsMod.MODID, name = TransitionsMod.NAME, version = TransitionsMod.VERSION, clientSideOnly = true)
public class TransitionsMod {
    public static final String MODID = "f5transitions";
    public static final String NAME = "F5 Transitions";
    public static final String VERSION = "@MOD_VERSION@"; // this will be replaced by Gradle

    public static final Logger LOGGER = LogManager.getLogger("F5 Transitions");

    @Mod.Instance(MODID)
    public static TransitionsMod INSTANCE;
    public TransitionsConfig config = new TransitionsConfig();

    public static boolean patcherLoadedInClasspath;

    public TransitionsMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        patcherLoadedInClasspath = Loader.isModLoaded("patcher");
        this.config.preload();
        ModCoreAPI.getCommandRegistry().registerCommand(new F5TransitionsCommand());
    }
}