# Simple Vertical Slabs

A Minecraft 1.12.2 Forge mod that adds a vertical version of every vanilla slab.

## Blocks

Stone, Sandstone, Cobblestone, Brick, Stone Brick, Nether Brick, Quartz, Red Sandstone, Purpur, and Oak, Spruce,
Birch, Jungle, Acacia and Dark Oak wood. Each matches its vanilla slab's textures, strength, sounds and map colour;
wooden ones burn like vanilla wooden slabs.

## Placing

- Right-clicking a single vertical slab's open side, or a face leading into its space, combines two into a full
  block.
- Clicking the narrow side, top or bottom of a vertical slab continues its wall in the same direction.
- Clicking the side of a block you're facing (within 45°) attaches the slab flat against it.
- Otherwise (floors, ceilings, or a side seen at a steeper angle) the slab stands across your view. Aim at the far
  part of the face to put it on the far half, or the near part for the near half.

## Recipes

- 3 full blocks in a column → 6 vertical slabs
- 1 vanilla slab → 1 vertical slab, and back

## Building

Requires JDK 8.

```
gradlew build
```

The mod jar is written to `build/libs/simpleverticalslabs-1.0.jar`.
