package dev.userteemu.f5transitions.config

import gg.essential.vigilance.Vigilant.CategoryPropertyBuilder
import dev.userteemu.f5transitions.utils.readableName
import net.minecraft.block.*
import kotlin.reflect.KClass

enum class CameraCollisionException(private vararg val classes: KClass<*>, val description: String = "") {
    GRASS_AND_PLANTS(
        BlockTallGrass::class,
        BlockDoublePlant::class,
        BlockFlower::class,
        BlockSapling::class,
        BlockMushroom::class,
        description = "Grass, 2 blocks high plants, flowers, saplings and mushrooms"
    ),
    CROPS(BlockCrops::class, BlockNetherWart::class),
    COBWEBS(BlockWeb::class),
    STRINGS(BlockTripWire::class),
    PORTALS(BlockPortal::class),
    VINES(BlockVine::class),
    STEMS(BlockStem::class),
    SUGAR_CANES(BlockReed::class),
    LILY_PADS(BlockLilyPad::class),
    DEAD_BUSHES(BlockDeadBush::class),
    BANNERS(BlockBanner::class),
    TORCHES(BlockTorch::class),
    REDSTONE_DUST(BlockRedstoneWire::class),
    SIGNS(BlockSign::class),
    BARRIER_BLOCKS(BlockBarrier::class),
    PANES(BlockPane::class),
    GLASS(BlockGlass::class);

    private var isEnabled = false

    fun cameraCanGoThroughBlock(block: Block?): Boolean {
        return isEnabled && classes.any { it.isInstance(block) }
    }

    fun asVigilanceSwitch(categoryPropertyBuilder: CategoryPropertyBuilder) {
        return categoryPropertyBuilder.switch(
            ::isEnabled,
            readableName,
            description
        )
    }

    companion object {
        /**
         * Master toggle
         */
        var changeCollidableBlocks = false

        fun cameraCanGoThroughBlock(block: Block?): Boolean {
            return changeCollidableBlocks && values().any { it.cameraCanGoThroughBlock(block) }
        }
    }
}