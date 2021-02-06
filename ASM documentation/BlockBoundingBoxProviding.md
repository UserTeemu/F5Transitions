# Block
Adds a new method for blocks to extend.

The method allows a block's class to add the block's bounding boxes to a set of AABBs. The set is used by RaytracingUtil, which runs camera distance ray tracing.

Any block can extend this new method, but the only stair blocks implement them in this mod, because they are the only blocks that have multiple boxes in their collision. That implementation is covered in the [BlockStairTransformer](#blockstairs) section.

```java
    public void addAllBoundingBoxesToSetForCameraRayTracing(World worldIn, BlockPos pos, IBlockState iblockstate, Set<AxisAlignedBB> boundingBoxes) {

    }
```

# BlockStairs
Extends the `addAllBoundingBoxesToSetForCameraRayTracing` method from Block in BlockStair.  
The method adds the block's bounding boxes to the set of AABBs. The set is used by RaytracingUtil, which runs camera distance ray tracing.

This method's code is based on `BlockStairs.collisionRayTrace`.
```java
    public void addAllBoundingBoxesToSetForCameraRayTracing(World worldIn, BlockPos pos, IBlockState iblockstate, Set<AxisAlignedBB> set) {
        int i = iblockstate.getValue(FACING).getHorizontalIndex();
        boolean isTop = iblockstate.getValue(HALF) == BlockStairs.EnumHalf.TOP;
        int[] aint = field_150150_a[i + (isTop?4:0)];
        this.hasRaytraced = true;

        for (int j = 0; j < 8; ++j) {
            this.rayTracePass = j;

            if (Arrays.binarySearch(aint, j) < 0) {
                setBlockBoundsBasedOnState(worldIn, pos);
                set.add(new AxisAlignedBB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ));
            }
        }
    }
```

# Calling the new method

Calls to the new method are done by calling a placeholder method `io.github.tivj.f5transitions.asm.BlockPlaceholder.addAllBoundingBoxesToSetForCameraRayTracing`.
The placeholder method's bytecode is injected to call the real method.

You may ask why not just inject the real method call to the place where the placeholder method is called?  
Easy answer: I didn't get it to work and I am too lazy to make it work.