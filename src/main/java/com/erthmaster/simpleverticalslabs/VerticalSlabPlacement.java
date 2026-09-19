package com.erthmaster.simpleverticalslabs;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Decides where a vertical slab goes when a player right-clicks with one.
 *
 *  - Clicking the open side of a single slab, or any face leading into a single slab's space, fills it into a
 *    double slab.
 *  - Clicking the left or right side, top or bottom of a single vertical slab continues its wall: the new slab
 *    faces the same way.
 *  - Clicking the side of any other block that the player faces (within 45 degrees) attaches the slab flat against
 *    it, facing away from it: clicking a block's north face while looking south gives a slab facing north.
 *  - Otherwise (a top or bottom face, or a side face seen at a steeper angle) the slab stands across the player's
 *    view, using one of the 4 directions like stairs. Aiming at the far part of the face puts it on the far half,
 *    open side toward the player (the same as clicking a block's side head-on); aiming at the near part puts it on
 *    the near half, open side away. Aiming left or right makes no difference.
 */
public final class VerticalSlabPlacement {

    public final BlockPos pos;
    public final IBlockState state;

    private VerticalSlabPlacement(BlockPos pos, IBlockState state) {
        this.pos = pos;
        this.state = state;
    }

    /** Where clicking a face with the slab would put it, or null if it can't go there. Ignores entities. */
    @Nullable
    public static VerticalSlabPlacement find(World world, BlockVerticalSlab block, EntityLivingBase placer,
                                             BlockPos clickedPos, EnumFacing face, float hitX, float hitZ) {
        IBlockState clicked = world.getBlockState(clickedPos);
        if (isSingleSlabOf(block, clicked) && face == clicked.getValue(BlockVerticalSlab.FACING)) {
            return new VerticalSlabPlacement(clickedPos, clicked.withProperty(BlockVerticalSlab.DOUBLE, true));
        }

        BlockPos target = clicked.getBlock().isReplaceable(world, clickedPos) ? clickedPos : clickedPos.offset(face);
        IBlockState existing = world.getBlockState(target);
        if (isSingleSlabOf(block, existing)) {
            return new VerticalSlabPlacement(target, existing.withProperty(BlockVerticalSlab.DOUBLE, true));
        }
        if (!existing.getBlock().isReplaceable(world, target)) {
            return null;
        }

        EnumFacing facing = chooseFacing(face, singleSlabFacing(clicked), placer.getHorizontalFacing(), hitX, hitZ);
        IBlockState state = block.getDefaultState().withProperty(BlockVerticalSlab.FACING, facing);
        return new VerticalSlabPlacement(target, state);
    }

    /** False if an entity, including the placing player, is in the way of the half being placed. */
    public boolean isUnobstructed(World world) {
        return world.checkNoEntityCollision(state.getCollisionBoundingBox(world, pos).offset(pos));
    }

    /**
     * The FACING for a new slab: the direction its open side faces.
     *
     * @param face          the clicked face, pointing from the clicked block toward the new slab
     * @param clickedSlab   the facing of the clicked block if it's a single vertical slab, otherwise null
     * @param lookDirection the horizontal direction the player faces
     * @param hitX          where the click landed on the clicked block, 0 to 1 (hitZ likewise)
     */
    static EnumFacing chooseFacing(EnumFacing face, @Nullable EnumFacing clickedSlab, EnumFacing lookDirection,
                                   float hitX, float hitZ) {
        if (clickedSlab != null && face.getAxis() != clickedSlab.getAxis()) {
            return clickedSlab;
        }
        if (face.getAxis() == lookDirection.getAxis()) {
            return face;
        }
        // How far past the face's centre the aim point is, measured along the player's view
        float ahead = (hitX - 0.5f) * lookDirection.getFrontOffsetX() + (hitZ - 0.5f) * lookDirection.getFrontOffsetZ();
        return ahead >= 0 ? lookDirection.getOpposite() : lookDirection;
    }

    /** The facing of a single vertical slab of any type, or null for anything else. */
    @Nullable
    static EnumFacing singleSlabFacing(IBlockState state) {
        return state.getBlock() instanceof BlockVerticalSlab && !BlockVerticalSlab.isDouble(state)
                ? state.getValue(BlockVerticalSlab.FACING) : null;
    }

    private static boolean isSingleSlabOf(BlockVerticalSlab block, IBlockState state) {
        return state.getBlock() == block && !BlockVerticalSlab.isDouble(state);
    }
}
