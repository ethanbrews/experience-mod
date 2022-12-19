package me.ethanbrews.experience.utility

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import kotlin.math.sqrt

object BlockPosHelper {
    fun blockPosFromList(arr: List<Int>): BlockPos = BlockPos(arr[0], arr[1], arr[2])
    fun blockPosFromArray(arr: IntArray): BlockPos = BlockPos(arr[0], arr[1], arr[2])

    fun magnitude(vec: Vec3i): Double =
        sqrt(listOf(vec.x, vec.y, vec.z).map { it * it }.reduce { acc, i -> acc + i }.toDouble())

    fun unitVector(vec: Vec3i): Vec3d =
        magnitude(vec).let { mag -> Vec3d(vec.x/mag, vec.y/mag, vec.z/mag) }
    fun findVectorBetween(a: BlockPos, b: BlockPos): Vec3i =
        Vec3i(a.x - b.x, a.y - b.y, a.z - b.z)

    fun findUnitVectorBetween(a: BlockPos, b: BlockPos): Vec3d =
        unitVector(findVectorBetween(a, b))
}

fun BlockPos.toArray(): Array<Int> = arrayOf(this.x, this.y, this.z)

fun BlockPos.toList(): List<Int> = listOf(this.x, this.y, this.z)