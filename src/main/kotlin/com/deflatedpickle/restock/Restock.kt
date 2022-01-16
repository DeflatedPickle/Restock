/* Copyright (c) 2021-2022 DeflatedPickle under the MIT license */

package com.deflatedpickle.restock

import net.fabricmc.api.ClientModInitializer
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.text.TranslatableText

@Suppress("UNUSED")
object Restock : ClientModInitializer {
    private const val MOD_ID = "$[id]"
    private const val NAME = "$[name]"
    private const val GROUP = "$[group]"
    private const val AUTHOR = "$[author]"
    private const val VERSION = "$[version]"

    override fun onInitializeClient() {
        println(listOf(MOD_ID, NAME, GROUP, AUTHOR, VERSION))
    }

    fun count(preCount: Int, context: ItemUsageContext) {
        context.player?.let { player ->
            val stack = player.getStackInHand(player.activeHand)

            if (!stack.isEmpty && stack.count < preCount) {
                val other = player.inventory.main.firstOrNull { s: ItemStack -> s != stack && stack.item == s.item }

                if (other != null) {
                    other.decrement(1)

                    if (player.inventory.main
                        .filter { s: ItemStack -> s != stack && stack.item == s.item }
                        .map { it.count }
                        .fold(0) { i, i1 -> i + i1 } <= 0
                    ) {
                        player.sendMessage(
                            TranslatableText("actionbar.finishStack"),
                            true
                        )
                    }

                    stack.increment(1)

                    player.inventory.main[player.inventory.selectedSlot].bobbingAnimationTime = 5
                }
            }
        }
    }
}
