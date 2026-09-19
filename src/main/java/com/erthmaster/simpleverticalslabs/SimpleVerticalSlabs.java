package com.erthmaster.simpleverticalslabs;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = SimpleVerticalSlabs.MODID, name = SimpleVerticalSlabs.NAME, version = SimpleVerticalSlabs.VERSION)
public class SimpleVerticalSlabs {

    public static final String MODID = "simpleverticalslabs";
    public static final String NAME = "Simple Vertical Slabs";
    public static final String VERSION = "1.0";

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ModBlocks.registerFlammability();
    }
}
