/* Copyright (c) 2021-2022 DeflatedPickle under the MIT license */

@file:Suppress("MemberVisibilityCanBePrivate", "UNUSED_PARAMETER")

package com.deflatedpickle.restock

import com.mojang.blaze3d.platform.InputUtil
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBind
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemUsageContext
import net.minecraft.text.Text.translatable
import org.lwjgl.glfw.GLFW
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer

@Suppress("UNUSED")
object Restock : ClientModInitializer {
    private const val MOD_ID = "$[id]"
    private const val NAME = "$[name]"
    private const val GROUP = "$[group]"
    private const val AUTHOR = "$[author]"
    private const val VERSION = "$[version]"

    var enabled = true

    private val toggleKeyBinding = KeyBind(
        "key.$MOD_ID.toggle",
        InputUtil.Type.KEYSYM,
        GLFW.GLFW_KEY_APOSTROPHE,
        "key.$MOD_ID"
    )

    override fun onInitializeClient(mod: ModContainer) {
        println(listOf(MOD_ID, NAME, GROUP, AUTHOR, VERSION))

        KeyBindingHelper.registerKeyBinding(toggleKeyBinding)
        ClientTickEvents.END_CLIENT_TICK.register(::onTick)
    }

    fun onTick(client: MinecraftClient) {
        when {
            toggleKeyBinding.wasPressed() -> enabled = !enabled
        }
    }

    fun count(preCount: Int, context: ItemUsageContext) {
        if (!enabled) return

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
                            translatable("actionbar.finishStack"),
                            true
                        )
                    }

                    stack.increment(1)

                    player.inventory.main[player.inventory.selectedSlot].cooldown = 5
                }
            }
        }
    }
}
