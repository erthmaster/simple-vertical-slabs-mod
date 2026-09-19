package com.erthmaster.simpleverticalslabs;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Places vertical slabs where {@link VerticalSlabPlacement} decides, including completing single slabs into
 * double ones. Replaces ItemBlock's placement entirely: its entity check tests the block's default half rather than
 * the half actually being placed, which let slabs be placed inside the player and refused valid spots.
 */
public class ItemBlockVerticalSlab extends ItemBlock {

    public ItemBlockVerticalSlab(BlockVerticalSlab block) {
        super(block);
    }

    @Nullable
    public VerticalSlabPlacement findPlacement(World world, EntityPlayer player, BlockPos pos, EnumFacing facing,
                                               float hitX, float hitZ) {
        return VerticalSlabPlacement.find(world, (BlockVerticalSlab) block, player, pos, facing, hitX, hitZ);
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing,
                                      float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        VerticalSlabPlacement placement = findPlacement(world, player, pos, facing, hitX, hitZ);
        if (stack.isEmpty() || placement == null || !player.canPlayerEdit(placement.pos, facing, stack)
                || !placement.isUnobstructed(world)) {
            return EnumActionResult.FAIL;
        }

        if (placeBlockAt(stack, player, world, placement.pos, facing, hitX, hitY, hitZ, placement.state)) {
            IBlockState placed = world.getBlockState(placement.pos);
            SoundType sound = placed.getBlock().getSoundType(placed, world, placement.pos, player);
            world.playSound(player, placement.pos, sound.getPlaceSound(), SoundCategory.BLOCKS,
                    (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
            stack.shrink(1);
        }
        return EnumActionResult.SUCCESS;
    }

    /**
     * Lists this slab right after its vanilla slab in creative tabs, instead of at the end with other mods' items.
     * Tabs fill one shared list in registration order, so the vanilla slabs are already in it; vertical slabs of the
     * same vanilla slab were registered in its variant order and each goes after the ones before it.
     */
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) {
            return;
        }
        Block vanillaSlab = vanillaSlabOf(this);
        Item vanillaSlabItem = Item.getItemFromBlock(vanillaSlab);
        for (int i = items.size() - 1; i >= 0; i--) {
            Item item = items.get(i).getItem();
            if (item == vanillaSlabItem || vanillaSlabOf(item) == vanillaSlab) {
                items.add(i + 1, new ItemStack(this));
                return;
            }
        }
        items.add(new ItemStack(this));
    }

    @Nullable
    private static Block vanillaSlabOf(Item item) {
        return item instanceof ItemBlockVerticalSlab
                ? ((BlockVerticalSlab) ((ItemBlockVerticalSlab) item).block).getVanillaSlab() : null;
    }

    // Wooden vertical slabs burn as long as vanilla wooden slabs (otherwise wood blocks default to a full 300)
    @Override
    public int getItemBurnTime(ItemStack itemStack) {
        return block.getDefaultState().getMaterial() == Material.WOOD ? 150 : -1;
    }

    // The client won't send the click to the server if this says no. The hit position isn't passed here, but it
    // only changes which half is chosen, not whether there's room for a slab.
    @SideOnly(Side.CLIENT)
    @Override
    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack) {
        return findPlacement(world, player, pos, side, 0.5f, 0.5f) != null;
    }
}
