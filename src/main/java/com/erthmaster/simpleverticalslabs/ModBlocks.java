package com.erthmaster.simpleverticalslabs;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockSand;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(modid = SimpleVerticalSlabs.MODID)
public class ModBlocks {

    /** A vertical version of every craftable vanilla slab, matching its material, strength, sound and map colour. */
    public static final BlockVerticalSlab[] VERTICAL_SLABS = {
            stoneSlab("stone", Blocks.STONE_SLAB, MapColor.STONE),
            stoneSlab("sandstone", Blocks.STONE_SLAB, MapColor.SAND),
            stoneSlab("cobblestone", Blocks.STONE_SLAB, MapColor.STONE),
            stoneSlab("brick", Blocks.STONE_SLAB, MapColor.RED),
            stoneSlab("stone_brick", Blocks.STONE_SLAB, MapColor.STONE),
            stoneSlab("nether_brick", Blocks.STONE_SLAB, MapColor.NETHERRACK),
            stoneSlab("quartz", Blocks.STONE_SLAB, MapColor.QUARTZ),
            stoneSlab("red_sandstone", Blocks.STONE_SLAB2, BlockSand.EnumType.RED_SAND.getMapColor()),
            stoneSlab("purpur", Blocks.PURPUR_SLAB, MapColor.MAGENTA),
            woodSlab(BlockPlanks.EnumType.OAK),
            woodSlab(BlockPlanks.EnumType.SPRUCE),
            woodSlab(BlockPlanks.EnumType.BIRCH),
            woodSlab(BlockPlanks.EnumType.JUNGLE),
            woodSlab(BlockPlanks.EnumType.ACACIA),
            woodSlab(BlockPlanks.EnumType.DARK_OAK),
    };

    private static BlockVerticalSlab stoneSlab(String material, Block vanillaSlab, MapColor mapColor) {
        return new BlockVerticalSlab(material + "_vertical_slab", vanillaSlab, Material.ROCK, mapColor, SoundType.STONE,
                10.0f, "pickaxe");
    }

    private static BlockVerticalSlab woodSlab(BlockPlanks.EnumType wood) {
        return new BlockVerticalSlab(wood.getName() + "_vertical_slab", Blocks.WOODEN_SLAB, Material.WOOD,
                wood.getMapColor(), SoundType.WOOD, 5.0f, "axe");
    }

    /** Lets wooden vertical slabs catch fire like vanilla wooden slabs. Call during init. */
    public static void registerFlammability() {
        for (BlockVerticalSlab slab : VERTICAL_SLABS) {
            if (slab.getDefaultState().getMaterial() == Material.WOOD) {
                Blocks.FIRE.setFireInfo(slab, 5, 20);
            }
        }
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(VERTICAL_SLABS);
    }

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
        for (BlockVerticalSlab slab : VERTICAL_SLABS) {
            event.getRegistry().register(new ItemBlockVerticalSlab(slab).setRegistryName(slab.getRegistryName()));
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        for (BlockVerticalSlab slab : VERTICAL_SLABS) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(slab), 0,
                    new ModelResourceLocation(slab.getRegistryName(), "inventory"));
        }
    }
}
