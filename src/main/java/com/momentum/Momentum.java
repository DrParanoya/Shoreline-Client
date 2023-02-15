package com.momentum;

import com.momentum.api.event.EventBus;
import com.momentum.impl.managers.ModuleManager;
import com.momentum.impl.registers.ConfigRegistry;
import com.momentum.impl.registers.ModuleRegistry;
import com.momentum.impl.init.Configs;
import com.momentum.impl.managers.TickManager;
import com.momentum.impl.ui.ClickGuiScreen;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * @author linus
 * @since 01/09/2023
 */
@Mod(modid = Momentum.MOD_ID, name = Momentum.MOD_NAME, version = Momentum.MOD_VERSION, acceptedMinecraftVersions = "[1.12.2]")
public class Momentum {

    // forge mod info
    public static final String MOD_ID = "momentum";
    public static final String MOD_NAME = "Momentum";
    public static final String MOD_VERSION = "1.0";

    // client info
    public static String CLIENT_ID = "momentum";
    public static String CLIENT_NAME = "Momentum";
    public static String CLIENT_VERSION = "1.0";

    // event bus
    public static EventBus EVENT_BUS;

    // registries
    public static ModuleRegistry MODULE_REGISTRY;
    public static ConfigRegistry CONFIG_REGISTRY;

    // managers
    public static ModuleManager MODULE_MANAGER;
    public static TickManager TICK_MANAGER;

    // click gui
    public static ClickGuiScreen CLICK_GUI;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        // create event bus
        EVENT_BUS = new EventBus();
        // logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {

        // initialize registries
        MODULE_REGISTRY = new ModuleRegistry();
        CONFIG_REGISTRY = new ConfigRegistry();

        // initialize managers
        MODULE_MANAGER = new ModuleManager();
        TICK_MANAGER = new TickManager();

        // initialize click gui
        CLICK_GUI = new ClickGuiScreen();

        // catches exception
        try {

            // attempt load
            Configs.save();
            Configs.load();
        }

        // failed to load
        catch (Exception e) {
            e.printStackTrace();
        }

        // some example code
        // logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }
}
