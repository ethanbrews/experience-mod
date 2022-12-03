package me.ethanbrews.experience.blocks.sentientStand

import me.ethanbrews.experience.items.SentientStaff
import me.ethanbrews.experience.recipe.EnchantmentRecipe
import me.ethanbrews.experience.registry.BlockRegistry
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.Packet
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3i
import net.minecraft.world.World

typealias GameTick = Int


class SentientStandEntity(pos: BlockPos, state: BlockState) : BlockEntity(BlockRegistry.sentient_stand_entity, pos, state) {
    private var _inventory = SimpleInventory(1)
    private var _event: SentientStandEvent? = null

    var stack: ItemStack
        get() = _inventory.getStack(0)
        set(value) {
            _inventory.setStack(0, value)
        }

    val isLocked : Boolean
        get() { return _event != null }

    private val _isMasterBlock: Boolean
        get() { return _event?.let { it.masterPos == this.pos } == true }

    public fun setEvent(event: SentientStandEvent) {
        _event = event
    }

    /**
     * Check the structure is valid. This must be the center block.
     * A structure is valid if it contains all tier 1 blocks or all tier 1 and 2 blocks.
     * Structures are NOT valid if a tier is partially present.
     *
     * @param world Minecraft world
     * @return true if the structure is valid and this is the center block
     */
    private fun isValidStructure(world: World): Boolean {
        return checkTier(world) > 0
    }

    private fun checkTier(world: World): Int {
        fun checkIt(it: Vec3i): Boolean = world.getBlockEntity(this.pos.add(it)) is SentientStandEntity
        if (!tier1Positions.all { checkIt(it) })
            return 0
        if (tier2Positions.all { checkIt(it )})
            return 2
        if (tier2Positions.any { checkIt(it) })
            return 0
        return 1
    }

    private fun getOthers(world: World): List<SentientStandEntity> {
        return when (checkTier(world)) {
            1 -> tier1Positions.map {
                world.getBlockEntity(this.pos.add(it)) as SentientStandEntity
            }
            2 -> (tier1Positions + tier2Positions).map {
                world.getBlockEntity(this.pos.add(it)) as SentientStandEntity
            }
            else -> listOf()
        }
    }

    /**
     * Interact with a player's inventory.
     * Either add or remove an item or do nothing
     * @param player the player that is interacting with the pedestal
     * @return the result of the action.
     */
    private fun interactWithPlayerInventory(player: PlayerEntity): ActionResult {
        if (isLocked)
            return ActionResult.FAIL

        val playerItem: ItemStack = player.inventory.getStack(player.inventory.selectedSlot)
        val blockItem: ItemStack = stack

        if (blockItem == ItemStack.EMPTY) {
            if (playerItem != ItemStack.EMPTY) {
                if (playerItem.count > 1) {
                    playerItem.count -= 1
                    player.inventory.setStack(player.inventory.selectedSlot, playerItem)
                    stack = playerItem.copy()
                    stack.count = 1
                } else {
                    stack = playerItem
                    player.inventory.setStack(player.inventory.selectedSlot, ItemStack.EMPTY)
                }
                return ActionResult.SUCCESS
            }
        } else {
            //prefer the current slot if eligible
            val eligibleSlot: Int = if (playerItem.isEmpty || (playerItem.count < playerItem.item.maxCount && playerItem.item == stack.item)) {
                player.inventory.selectedSlot
            } else {
                val firstSlotWithStack = player.inventory.getSlotWithStack(stack)
                val firstEmptySlot = player.inventory.emptySlot
                if (firstSlotWithStack >= 0 && player.inventory.getStack(firstSlotWithStack).count < stack.maxCount) {
                    firstSlotWithStack
                } else if (firstEmptySlot >= 0) {
                    firstEmptySlot
                } else {
                    return ActionResult.FAIL
                }
            }
            val eligibleStack = player.inventory.getStack(eligibleSlot)
            if (eligibleStack.isEmpty) {
                player.inventory.setStack(eligibleSlot, stack)
            } else {
                eligibleStack.count += 1
                player.inventory.setStack(eligibleSlot, eligibleStack)
            }
            stack = ItemStack.EMPTY
        }

        if (playerItem == ItemStack.EMPTY && blockItem != ItemStack.EMPTY) {
            player.inventory.setStack(player.inventory.selectedSlot, blockItem)
            stack = ItemStack.EMPTY
            return ActionResult.SUCCESS
        } else if (playerItem != ItemStack.EMPTY && blockItem == ItemStack.EMPTY) {
            player.inventory.setStack(player.inventory.selectedSlot, ItemStack.EMPTY)
            stack = playerItem
            return ActionResult.CONSUME
        }
        return ActionResult.PASS
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        nbt.put("inventory", _inventory.toNbtList())
        if (_event != null)
            nbt.put("event", _event!!.toNbt())
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        _inventory = SimpleInventory(1)
        _inventory.readNbtList(nbt.getList("inventory", 10))
        _event = if (nbt.contains("event"))
            SentientStandEvent.fromNbt(nbt.getCompound("event"))
        else
            null
    }

    fun playerUse(player: PlayerEntity): ActionResult {
        val item: Item = player.inventory.getStack(player.inventory.selectedSlot).item
        var result = ActionResult.PASS
        if (item is SentientStaff) {

            world?.let {world ->
                if (isValidStructure(world)) {
                    val auxiliaryStands = getOthers(world)
                    val recipe = EnchantmentRecipe.recipes.firstOrNull { recipe ->
                        recipe.validate(auxiliaryStands.map { it.stack })
                    }
                    if (recipe == null) {
                        player.sendMessage(Text.literal("No recipe found!"), true)
                    } else {
                        _event = SentientStandEvent(player, this.pos, SETUP_INTERVAL+FINISH_INTERNVAL+(ITEM_INTERVAL*4*checkTier(world)))
                        auxiliaryStands.forEach { it.setEvent(_event!!) }
                    }
                }
            }
        } else {
            result = interactWithPlayerInventory(player)
        }
        sendUpdatePacket()
        return result
    }

    fun tick(world: World) {
        if (!_isMasterBlock)
            return
        if (world.isClient) {

        } else {
            _event = _event?.run {

                _event
            }
        }
    }

    private fun sendUpdatePacket() {
        world?.updateListeners(pos, cachedState, cachedState, Block.NOTIFY_LISTENERS);
    }

    override fun toUpdatePacket(): Packet<ClientPlayPacketListener?>? {
        return BlockEntityUpdateS2CPacket.create(this)
    }

    override fun toInitialChunkDataNbt(): NbtCompound? {
        return createNbt()
    }

    companion object {
        val tier1Positions = listOf(
            Vec3i(-2, 0, 0),
            Vec3i(2, 0, 0),
            Vec3i(0, 0, 2),
            Vec3i(0, 0, -2)
        )

        val tier2Positions = listOf(
            Vec3i(-2, 0, -2),
            Vec3i(-2, 0, 2),
            Vec3i(2, 0, -2),
            Vec3i(2, 0, 2)
        )

        val SETUP_INTERVAL: GameTick =   80
        val ITEM_INTERVAL: GameTick =    30
        val FINISH_INTERNVAL: GameTick = 80

        fun tick(world: World, pos: BlockPos, state: BlockState, be: SentientStandEntity) {
            be.tick(world)
        }
    }
}