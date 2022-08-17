package me.ethanbrews.experience.blocks.sentientStand

import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.render.block.entity.BlockEntityRenderer
import net.minecraft.client.render.model.json.ModelTransformation
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.util.math.Vec3f.POSITIVE_Y
import kotlin.math.sin

class SentientStandRenderer : BlockEntityRenderer<SentientStandEntity> {
    override fun render(
        entity: SentientStandEntity,
        tickDelta: Float,
        matrices: MatrixStack,
        vertexConsumers: VertexConsumerProvider,
        light: Int,
        overlay: Int
    ) {
        matrices.push()
        if (!entity.stack.isEmpty) {
            val offset = sin(((entity.world?.time ?: 0) + tickDelta).toDouble() / 8.0) / 4.0
            matrices.translate(0.5, 1.25 + offset, 0.5)
            matrices.multiply((entity.world?.time?.plus(tickDelta))?.times(4)?.let { POSITIVE_Y.getDegreesQuaternion(it) })

            val lightAbove = WorldRenderer.getLightmapCoordinates(entity.world!!, entity.pos.up())
            MinecraftClient.getInstance().itemRenderer.renderItem(entity.stack, ModelTransformation.Mode.GROUND, lightAbove, overlay, matrices, vertexConsumers, 0)
        }
        matrices.pop()
    }
}