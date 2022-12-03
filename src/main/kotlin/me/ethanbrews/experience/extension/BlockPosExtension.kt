package me.ethanbrews.experience.extension

import net.minecraft.util.math.BlockPos
fun blockPosFromList(arr: List<Int>): BlockPos = BlockPos(arr[0], arr[1], arr[2])
fun BlockPos.toList(): List<Int> = listOf(this.x, this.y, this.z)

fun blockPosFromArray(arr: IntArray): BlockPos = BlockPos(arr[0], arr[1], arr[2])
fun BlockPos.toArray(): Array<Int> = arrayOf(this.x, this.y, this.z)