package me.ethanbrews.experience.network

import me.ethanbrews.experience.blocks.sentientStand.SentientStandEntity
import me.ethanbrews.experience.modid
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos

object Packets {
    private val UPDATE_BLOCK_ENTITY_PACKET: Identifier = Identifier(modid, "block_entity_update")

    fun registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(UPDATE_BLOCK_ENTITY_PACKET) { client: MinecraftClient, handler: ClientPlayNetworkHandler, buffer: PacketByteBuf, sender: PacketSender ->
            val position: BlockPos = buffer.readBlockPos()
            val id = BlockEntityUpdatePacketID.valueOf(buffer.readString())
            val nbt = buffer.readNbt()

            client.execute {
                val entity = MinecraftClient.getInstance().world!!.getBlockEntity(position)
                // To extend this, add ONLY TO THE IF CONDITION!
                // if ((id == BlockEntityUpdatePacketID.X && entity is X) || (id == BlockEntityUpdatePacketID.Y && entity is Y)...)
                if (id == BlockEntityUpdatePacketID.RITUAL && entity is SentientStandEntity) {
                    entity.readNbt(nbt)
                }
            }
        }
    }

    fun sendBlockEntityUpdate(entity: BlockEntity, packetID: BlockEntityUpdatePacketID) {
        val buf = PacketByteBufs.create()
        buf.writeBlockPos(entity.pos)
        buf.writeString(packetID.toString())
        buf.writeNbt(entity.createNbt())

        for (player in PlayerLookup.tracking(entity.world as ServerWorld?, entity.pos)) {
            ServerPlayNetworking.send(player, UPDATE_BLOCK_ENTITY_PACKET, buf)
        }
    }
}