package me.ethanbrews.experience.blocks.sentientStand

import me.ethanbrews.experience.items.SentientStaff
import me.ethanbrews.experience.recipe.SentientRitualRecipe
import me.ethanbrews.experience.registry.BlockRegistry
import me.ethanbrews.experience.utility.BlockPosHelper
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.Packet
import net.minecraft.network.listener.ClientPlayPacketListener
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket
import net.minecraft.particle.ItemStackParticleEffect
import net.minecraft.particle.ParticleTypes
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3i
import net.minecraft.world.World
import kotlin.math.ceil
import kotlin.random.Random

typealias GameTick = Int


class SentientStandEntity(pos: BlockPos, state: BlockState) : BlockEntity(BlockRegistry.sentient_stand_entity, pos, state) {
    private var _inventory = SimpleInventory(1)
    private var _event: SentientStandEvent? = null

    /** stack represents the single stack contained within the internal inventory */
    var stack: ItemStack
        get() = _inventory.getStack(0)
        set(value) {
            _inventory.setStack(0, value)
            sendUpdatePacket()
        }

    /** The block entity is 'locked' if it is busy with an event */
    private val isLocked : Boolean
        get() { return _event != null }

    /** true if this is the center block (containing the target item rather than ingredient items) */
    private val _isMasterBlock: Boolean
        get() { return _event?.let { it.masterPos == this.pos } == true }

    /** The current event which may be null if nothing is happening. */
    private var event: SentientStandEvent?
        get() = _event
        set(value) {
            _event = value
            sendUpdatePacket()
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

    /**
     * Get the tier of the current structure (from the perspective of this entity which may not be the master block)
     * The structure is only valid if a complete tier is made of the present blocks.
     * A valid tier 1 structure becomes invalid if tier 2 is partially constructed.
     *
     * @param world Minecraft world
     * @return The tier where 0 represents an invalid structure
     */
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

    /**
     * Get the other blocks in this structure - from the perspective of this block which may not be the master.
     *
     * @param world Minecraft world
     * @return A list of surrounding [SentientStandEntity]'s that make the structure.
     */
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

    /** Write NBT for this block */
    override fun writeNbt(nbt: NbtCompound) {
        nbt.put("inventory", _inventory.toNbtList())
        if (_event != null)
            nbt.put("event", _event!!.toNbt())
        super.writeNbt(nbt)
    }

    /** Read NBT for this block */
    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        _inventory = SimpleInventory(1)
        _inventory.readNbtList(nbt.getList("inventory", 10))
        _event = if (nbt.contains("event"))
            SentientStandEvent.fromNbt(nbt.getCompound("event"))
        else
            null
    }

    /**
     *  React to a player using the block.
     *  This will either interact with the inventory or attempt to begin a ritual.
     *
     *  @param player The entity using the block
     *  @return The resulting action
     */
    fun playerUse(player: PlayerEntity): ActionResult {
        val item: Item = player.inventory.getStack(player.inventory.selectedSlot).item
        var result = ActionResult.PASS
        val w = world
        if (item is SentientStaff && w != null && isValidStructure(w)) {

            val auxiliaryStands = getOthers(w)
            val recipe = SentientRitualRecipe.recipes.firstOrNull { recipe ->
                recipe.validate(auxiliaryStands.map { it.stack })
            }
            val cost = recipe?.getExperienceCost(player.experienceLevel) ?: 0
            if (recipe == null) {
                player.sendMessage(Text.translatable("ritual.message.invalidRecipe"), true)
            } else if (!recipe.canAcceptResult(stack)) {
                player.sendMessage(Text.translatable("ritual.message.invalidTarget"), true)
            } else if (player.experienceLevel < cost && !player.isCreative) {
                player.sendMessage(Text.translatable("ritual.message.notEnoughExperience"), true)
            } else {
                event = SentientStandEvent(
                    player.uuidAsString,
                    this.pos,
                    SETUP_INTERVAL+FINISH_INTERVAL+(ITEM_INTERVAL*4*checkTier(w)),
                    recipe
                )
                auxiliaryStands.forEach { it.event = event }
            }

        } else {
            result = interactWithPlayerInventory(player)
        }
        sendUpdatePacket()
        return result
    }

    /**
     * Common tick for client and server.
     * If valid and busy, it will trigger the tick Client/Server methods with their respective args.
     */
    fun tick(world: World) {
        if (!_isMasterBlock)
            return

        val event = _event ?: return
        val auxStands = getOthers(world)

        if (world.isClient)
            tickClient(world, event, auxStands)
        else
            tickServer(world, event, auxStands)
    }

    /** Tick on the client, spawn effects etc. */
    private fun tickClient(world: World, event: SentientStandEvent, auxiliaryStands: List<SentientStandEntity>) {
        val x = randomDouble(-2.5..2.5)
        val y = randomDouble(0.5..2.5)
        val z = randomDouble(-2.5..2.5)

        val vx = randomDouble(-0.1..0.1)
        val vy = randomDouble(-0.1..0.1)
        val vz = randomDouble(-0.1..0.1)

        when (getPhase(event.tickCounter, auxiliaryStands)) {

            SentientStandEventPhase.CHARGING -> {
                world.addParticle(ParticleTypes.ENCHANT, pos.x + x, pos.y + y, pos.z + z, vx, -0.1, vz)
                world.addParticle(ParticleTypes.PORTAL, pos.x + x, pos.y + y, pos.z + z, vx, -0.1, vz)
            }

            SentientStandEventPhase.CONSUME -> {
                getConsumingStand(event.tickCounter, auxiliaryStands)?.let { consumingStand ->
                    val direction = BlockPosHelper.findUnitVectorBetween(this.pos, consumingStand.pos).multiply(0.4)
                    if (isLastTickOfConsumption(event.tickCounter))
                        world.addParticle(
                            ParticleTypes.CLOUD,
                            consumingStand.pos.x + vx + 0.5, consumingStand.pos.y + vy + 1.5, consumingStand.pos.z + vz + 0.5,
                            0.0, 0.0, 0.0
                        )
                    else
                        world.addParticle(
                            ItemStackParticleEffect(ParticleTypes.ITEM, consumingStand.stack),
                            consumingStand.pos.x + vx + 0.5, consumingStand.pos.y + vy + 1.5, consumingStand.pos.z + vz + 0.5,
                            direction.x, direction.y, direction.z
                        )

                }
            }

            SentientStandEventPhase.FINISH -> {
                val vx2 = randomDouble(-0.4..0.4)
                val vy2 = randomDouble(0.1..0.3)
                val vz2 = randomDouble(-0.4..0.4)
                world.addParticle(ParticleTypes.ENCHANT, pos.x + vx2 + 0.5, pos.y + vy2 + 1.2, pos.z + vz2 + 0.5, vx2, vy2, vz2)
            }

            SentientStandEventPhase.FINISHED -> {
                for (i in 0 until 20) {
                    val vx2 = randomDouble(-0.4..0.4)
                    val vy2 = randomDouble(0.1..0.3)
                    val vz2 = randomDouble(-0.4..0.4)
                    world.addParticle(ParticleTypes.REVERSE_PORTAL, pos.x + vx2 + 0.5, pos.y + vy2 + 1.2, pos.z + vz2 + 0.5, vx2, vy2, vz2)
                }
            }

            else -> {}
        }
    }

    /** Tick on the server, progress the event and react to changes (consume items then apply effects). */
    private fun tickServer(world: World, event: SentientStandEvent, auxiliaryStands: List<SentientStandEntity>) {
        if (event.tickCounter == 0) {
            this.event = null
            return
        }

        event.tickCounter -= 1

        when(getPhase(event.tickCounter, auxiliaryStands)) {
            SentientStandEventPhase.SETUP -> {
                event.getPlayer(world)?.let { p ->
                    p.addExperience(-(event.recipe?.getExperienceCost(p.experienceLevel) ?: 0))
                }
            }

            SentientStandEventPhase.CONSUME -> {
                if (isLastTickOfConsumption(event.tickCounter)) {
                    getConsumingStand(event.tickCounter, auxiliaryStands)?.run {
                        this.stack = ItemStack.EMPTY
                        this.sendUpdatePacket()
                    }
                }
            }

            SentientStandEventPhase.FINISHED -> {
                stack = event.recipe?.getResult(stack) ?: stack
                auxiliaryStands.forEach { it.event = null }
            }

            else -> {}
        }

        // Set _event to avoid sending update packets when not needed.
        this._event = event

        if (isEventStateChangeTick(event.tickCounter, auxiliaryStands))
            sendUpdatePacket()
    }

    /** Send an update to the client. Also mark as dirty for saving to disk. */
    private fun sendUpdatePacket() {
        markDirty()
        world?.updateListeners(pos, cachedState, cachedState, Block.NOTIFY_LISTENERS)
    }

    /** Drop the contents of the internal inventory on the ground as it breaks */
    fun breaking(world: World) {
        _event?.let { e ->
            val master = (world.getBlockEntity(e.masterPos) as? SentientStandEntity)
            master?.event = null
            master?.getOthers(world)?.forEach {it.event = null }
        }
        world.spawnEntity(ItemEntity(
            world,
            pos.x.toDouble(),
            pos.y.toDouble(),
            pos.z.toDouble(),
            stack,
            0.0,
            0.3,
            0.0
        ))
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

        const val SETUP_INTERVAL: GameTick =   80
        const val ITEM_INTERVAL: GameTick =    30
        const val FINISH_INTERVAL: GameTick = 30

        private fun isEventStateChangeTick(count: Int, others: List<SentientStandEntity>): Boolean {
            return count == 0 ||
                    count == FINISH_INTERVAL + (ITEM_INTERVAL * others.size) ||
                    count == FINISH_INTERVAL - 1 ||
                    isLastTickOfConsumption(count - 1) ||
                    isLastTickOfConsumption(count) ||
                    isLastTickOfConsumption(count + 1)
        }

        private fun getPhase(count: Int, others: List<SentientStandEntity>): SentientStandEventPhase {
            return if (count == 0)
                SentientStandEventPhase.FINISHED
            else if (count == (FINISH_INTERVAL + SETUP_INTERVAL + (ITEM_INTERVAL * others.size)) - 1)
                SentientStandEventPhase.SETUP
            else if (count > FINISH_INTERVAL + (ITEM_INTERVAL * others.size))
                SentientStandEventPhase.CHARGING
            else if (count <= FINISH_INTERVAL)
                SentientStandEventPhase.FINISH
            else
                SentientStandEventPhase.CONSUME
        }

        fun randomDouble(range: ClosedFloatingPointRange<Double>): Double =
            Random.Default.nextDouble(range.start, range.endInclusive)

        fun isLastTickOfConsumption(count: Int) =
            ceil(standValue(count)).toInt() != ceil(standValue(count-1)).toInt()

        private fun standValue(count: Int) = ((count - FINISH_INTERVAL).toDouble() / ITEM_INTERVAL) - 1

        fun getConsumingStand(count: Int, others: List<SentientStandEntity>): SentientStandEntity? {
            return if (getPhase(count, others) == SentientStandEventPhase.CONSUME) {
                val standIndex = ceil(standValue(count)).toInt()
                others[standIndex]
            } else null
        }

        @Suppress("UNUSED_PARAMETER")
        fun tick(world: World, pos: BlockPos, state: BlockState, be: SentientStandEntity) {
            be.tick(world)
        }
    }
}