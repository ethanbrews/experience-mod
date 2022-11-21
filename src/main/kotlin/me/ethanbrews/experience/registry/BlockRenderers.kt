package me.ethanbrews.experience.registry

import me.ethanbrews.experience.blocks.sentientStand.SentientStandRenderer
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry

object BlockRenderers {
    fun register() {
        BlockEntityRendererRegistry.register(BlockRegistry.sentient_stand_entity) { SentientStandRenderer() }
    }
}