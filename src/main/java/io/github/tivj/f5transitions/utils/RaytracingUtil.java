package io.github.tivj.f5transitions.utils;

import io.github.tivj.f5transitions.asm.BlockPlaceholder;
import io.github.tivj.f5transitions.config.TransitionsConfig;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public class RaytracingUtil {
    /**
     * Based on net.minecraft.world.World#rayTraceBlocks
     * This method is used as a replacement for raytracing what block a camera in F5 will hit. Hitboxes of blocks are extended by 0.06 blocks (near plane clipping distance (0.05) + space for calculation inaccuracies) in each direction.
     * All boolean arguments of the original method have been hardcoded to be false (as they would be when calling it normally) for a smaller performance impact.
     * The hit side of the returning MovingObjectPosition is never used by MC so calculating that information would be useless and have an impact on performance. UP is passed as a dummy.
     */
    @SuppressWarnings("unused") // used by ASM
    public static MovingObjectPosition rayTraceBlocks(World world, Vec3 vec3, Vec3 endVec) {
        if (!Double.isNaN(vec3.xCoord) && !Double.isNaN(vec3.yCoord) && !Double.isNaN(vec3.zCoord) && !Double.isNaN(endVec.xCoord) && !Double.isNaN(endVec.yCoord) && !Double.isNaN(endVec.zCoord)) {
            int endX = MathHelper.floor_double(endVec.xCoord);
            int endY = MathHelper.floor_double(endVec.yCoord);
            int endZ = MathHelper.floor_double(endVec.zCoord);
            int posX = MathHelper.floor_double(vec3.xCoord);
            int posY = MathHelper.floor_double(vec3.yCoord);
            int posZ = MathHelper.floor_double(vec3.zCoord);

            for (int loops = 200; true; loops--) {
                BlockPos blockPos = new BlockPos(posX, posY, posZ);
                IBlockState blockState = world.getBlockState(blockPos);
                Block block = blockState.getBlock();

                Vec3 directionVec = endVec.subtract(vec3).normalize();
                Vec3 relativeStartVec = vec3.addVector(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ());

                block.setBlockBoundsBasedOnState(world, blockPos);
                if (block.canCollideCheck(blockState, false) && !TransitionsConfig.cameraCanGoThroughBlock(block)) {
                    Set<AxisAlignedBB> boundingBoxes = new HashSet<>();
                    BlockPlaceholder.addAllBoundingBoxesToSetForCameraRayTracing(block, world, blockPos, blockState, boundingBoxes);

                    //noinspection ConstantConditions - IntelliJ shouldn't warn that there are never any entries in boundingBoxes, because they are added with bytecode modification.
                    if (boundingBoxes.size() == 0) {
                        boundingBoxes.add(getAABBForBlock(block));
                    }

                    for (AxisAlignedBB box : boundingBoxes) {
                        box = expandAABB(box);
                        Vec3 hitVec = RaytracingUtil.raytraceAABB(box, relativeStartVec, directionVec);

                        if (hitVec != null) {
                            hitVec = hitVec.addVector(blockPos.getX(), blockPos.getY(), blockPos.getZ());
                            // The hit side is never used so calculating that information would be useless. EnumFacing.UP is given as a dummy.
                            return new MovingObjectPosition(hitVec.add(new Vec3(blockPos)), EnumFacing.UP, blockPos);
                        }
                    }
                }

                MovingObjectPosition m = raytraceSidesOfNeighbors(world, blockPos, relativeStartVec, directionVec);
                if (m != null) return m;

                if ((posX == endX && posY == endY && posZ == endZ) || loops < 0) {
                    return null;
                }

                boolean flagX = true;
                boolean flagY = true;
                boolean flagZ = true;
                double d0 = 999.0D;
                double d1 = 999.0D;
                double d2 = 999.0D;

                if (endX > posX) d0 = (double)posX + 1.0D;
                else if (endX < posX) d0 = posX;
                else flagX = false;

                if (endY > posY) d1 = (double)posY + 1.0D;
                else if (endY < posY) d1 = posY;
                else flagY = false;

                if (endZ > posZ) d2 = (double)posZ + 1.0D;
                else if (endZ < posZ) d2 = posZ;
                else flagZ = false;

                double d3 = 999D;
                double d4 = 999D;
                double d5 = 999D;
                double xDifference = endVec.xCoord - vec3.xCoord;
                double yDifference = endVec.yCoord - vec3.yCoord;
                double zDifference = endVec.zCoord - vec3.zCoord;

                if (flagX) d3 = (d0 - vec3.xCoord) / xDifference;
                if (flagY) d4 = (d1 - vec3.yCoord) / yDifference;
                if (flagZ) d5 = (d2 - vec3.zCoord) / zDifference;

                if (d3 == -0D) d3 = -1.0E-4D;
                if (d4 == -0D) d4 = -1.0E-4D;
                if (d5 == -0D) d5 = -1.0E-4D;

                EnumFacing enumFacing;

                if (d3 < d4 && d3 < d5) {
                    enumFacing = endX > posX ? EnumFacing.WEST : EnumFacing.EAST;
                    vec3 = new Vec3(d0, vec3.yCoord + yDifference * d3, vec3.zCoord + zDifference * d3);
                } else if (d4 < d5) {
                    enumFacing = endY > posY ? EnumFacing.DOWN : EnumFacing.UP;
                    vec3 = new Vec3(vec3.xCoord + xDifference * d4, d1, vec3.zCoord + zDifference * d4);
                } else {
                    enumFacing = endZ > posZ ? EnumFacing.NORTH : EnumFacing.SOUTH;
                    vec3 = new Vec3(vec3.xCoord + xDifference * d5, vec3.yCoord + yDifference * d5, d2);
                }

                posX = MathHelper.floor_double(vec3.xCoord) - (enumFacing == EnumFacing.EAST ? 1 : 0);
                posY = MathHelper.floor_double(vec3.yCoord) - (enumFacing == EnumFacing.UP ? 1 : 0);
                posZ = MathHelper.floor_double(vec3.zCoord) - (enumFacing == EnumFacing.SOUTH ? 1 : 0);
                if (Double.isNaN(vec3.xCoord) || Double.isNaN(vec3.yCoord) || Double.isNaN(vec3.zCoord)) {
                    return null;
                }
            }
        }
        return null;
    }

    private static AxisAlignedBB getAABBForBlock(Block block) {
        return new AxisAlignedBB(
            block.getBlockBoundsMinX(),
            block.getBlockBoundsMinY(),
            block.getBlockBoundsMinZ(),
            block.getBlockBoundsMaxX(),
            block.getBlockBoundsMaxY(),
            block.getBlockBoundsMaxZ()
        );
    }

    private static AxisAlignedBB expandAABB(AxisAlignedBB aabb) {
        return aabb.expand(0.1D, 0.1D, 0.1D);
    }

    private static MovingObjectPosition raytraceSidesOfNeighbors(World world, BlockPos centralPos, Vec3 start, Vec3 directionVec) {
        Vec3 raytracingResult = null;

        for (EnumFacing direction : EnumFacing.values()) {
            BlockPos iterationBlockPos = centralPos.offset(direction);
            IBlockState iterationBlockState = world.getBlockState(iterationBlockPos);
            Block iterationBlock = iterationBlockState.getBlock();

            if (!iterationBlock.canCollideCheck(iterationBlockState, false) || TransitionsConfig.cameraCanGoThroughBlock(iterationBlock)) continue;

            Set<AxisAlignedBB> boundingBoxes = new HashSet<>();
            BlockPlaceholder.addAllBoundingBoxesToSetForCameraRayTracing(iterationBlock, world, iterationBlockPos, iterationBlockState, boundingBoxes);

            //noinspection ConstantConditions - IntelliJ shouldn't warn that there are never any entries in boundingBoxes, because they are added with bytecode modification.
            if (boundingBoxes.size() == 0) {
                iterationBlock.setBlockBoundsBasedOnState(world, iterationBlockPos);
                boundingBoxes.add(getAABBForBlock(iterationBlock));
            }

            for (AxisAlignedBB aabb : boundingBoxes) {
                aabb = aabb.offset(iterationBlockPos.getX() - centralPos.getX(), iterationBlockPos.getY() - centralPos.getY(), iterationBlockPos.getZ() - centralPos.getZ()).expand(0.1D, 0.1D, 0.1D);
                Vec3 currentRaytracingResult = raytraceAABB(aabb, start, directionVec);

                if (raytracingResult == null || (currentRaytracingResult != null && currentRaytracingResult.distanceTo(start) < raytracingResult.distanceTo(start))) {
                    raytracingResult = currentRaytracingResult;
                }
            }
        }

        // The hit side is never used so calculating that information would be useless. EnumFacing.UP is given as a dummy.
        return raytracingResult == null ? null : new MovingObjectPosition(raytracingResult.add(new Vec3(centralPos)), EnumFacing.UP, centralPos);
    }

    /**
     * Checks if given ray will hit an axis aligned bounding box (AABB).
     * Based on https://gamedev.stackexchange.com/a/18459
     */
    public static Vec3 raytraceAABB(AxisAlignedBB aabb, Vec3 start, Vec3 direction) {
        double dirfracX = 1D / direction.xCoord;
        double dirfracY = 1D / direction.yCoord;
        double dirfracZ = 1D / direction.zCoord;

        double minX = (aabb.minX - start.xCoord) * dirfracX;
        double maxX = (aabb.maxX - start.xCoord) * dirfracX;
        double minY = (aabb.minY - start.yCoord) * dirfracY;
        double maxY = (aabb.maxY - start.yCoord) * dirfracY;
        double minZ = (aabb.minZ - start.zCoord) * dirfracZ;
        double maxZ = (aabb.maxZ - start.zCoord) * dirfracZ;

        double tmin = Math.max(Math.max(Math.min(minX, maxX), Math.min(minY, maxY)), Math.min(minZ, maxZ));
        double tmax = Math.min(Math.min(Math.max(minX, maxX), Math.max(minY, maxY)), Math.max(minZ, maxZ));

        if (tmax < 0) return null; // ray (line) is intersecting AABB, but the whole AABB is behind us
        if (tmin > tmax) return null; // ray doesn't intersect AABB

        return new Vec3(
            start.xCoord + direction.xCoord * tmin,
            start.yCoord + direction.yCoord * tmin,
            start.zCoord + direction.zCoord * tmin
        );
    }
}
