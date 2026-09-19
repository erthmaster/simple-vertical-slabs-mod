package com.erthmaster.simpleverticalslabs;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

/**
 * A half-block standing on its side. FACING is the direction the slab's open side faces, so the slab fills the
 * opposite half of its space: like a ladder or trapdoor, it faces away from the block it's attached to.
 * Two slabs of the same block combine into a full block (DOUBLE), which drops both slabs when broken.
 * Where a slab goes when placed is decided in {@link VerticalSlabPlacement}.
 */
public class BlockVerticalSlab extends Block {

    public static final PropertyDirection FACING = BlockHorizontal.FACING;
    public static final PropertyBool DOUBLE = PropertyBool.create("double");

    private static final AxisAlignedBB NORTH_HALF_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.5D);
    private static final AxisAlignedBB SOUTH_HALF_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.5D, 1.0D, 1.0D, 1.0D);
    private static final AxisAlignedBB WEST_HALF_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.5D, 1.0D, 1.0D);
    private static final AxisAlignedBB EAST_HALF_AABB = new AxisAlignedBB(0.5D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);

    private final Block vanillaSlab;

    public BlockVerticalSlab(String name, Block vanillaSlab, Material material, MapColor mapColor, SoundType sound,
                             float resistance, String tool) {
        super(material, mapColor);
        this.vanillaSlab = vanillaSlab;
        setUnlocalizedName(SimpleVerticalSlabs.MODID + "." + name);
        setRegistryName(SimpleVerticalSlabs.MODID, name);
        setHardness(2.0f);
        setResistance(resistance);
        setHarvestLevel(tool, 0);
        setSoundType(sound);
        setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(DOUBLE, false));
    }

    /** The vanilla slab block this is the vertical version of. */
    public Block getVanillaSlab() {
        return vanillaSlab;
    }

    public static boolean isDouble(IBlockState state) {
        return state.getValue(DOUBLE);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        if (isDouble(state)) {
            return FULL_BLOCK_AABB;
        }
        switch (state.getValue(FACING)) {
            case SOUTH: return NORTH_HALF_AABB;
            case WEST: return EAST_HALF_AABB;
            case EAST: return WEST_HALF_AABB;
            default: return SOUTH_HALF_AABB;
        }
    }

    // Players place through ItemBlockVerticalSlab; this covers anything else placing the block
    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
                                            int meta, EntityLivingBase placer) {
        IBlockState clicked = world.getBlockState(pos.offset(facing.getOpposite()));
        EnumFacing side = VerticalSlabPlacement.chooseFacing(facing, VerticalSlabPlacement.singleSlabFacing(clicked),
                placer.getHorizontalFacing(), hitX, hitZ);
        return getDefaultState().withProperty(FACING, side);
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
        return isDouble(state) || face == state.getValue(FACING).getOpposite() ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return getBlockFaceShape(world, state, pos, side) == BlockFaceShape.SOLID;
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        return isDouble(state) || face == state.getValue(FACING).getOpposite();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return isDouble(state);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return isDouble(state);
    }

    @Override
    public boolean causesSuffocation(IBlockState state) {
        return isDouble(state);
    }

    @Override
    public boolean getUseNeighborBrightness(IBlockState state) {
        return !isDouble(state);
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        return isDouble(state) ? 255 : 0;
    }

    @Override
    public int quantityDropped(IBlockState state, int fortune, Random random) {
        return isDouble(state) ? 2 : 1;
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirror) {
        return state.withRotation(mirror.toRotation(state.getValue(FACING)));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState()
                .withProperty(FACING, EnumFacing.getHorizontal(meta & 3))
                .withProperty(DOUBLE, (meta & 4) != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex() | (isDouble(state) ? 4 : 0);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING, DOUBLE);
    }
}
