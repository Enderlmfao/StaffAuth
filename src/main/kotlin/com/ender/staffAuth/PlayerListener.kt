package com.ender.staffAuth

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.*

class PlayerListener(private val plugin: StaffAuth) : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        if (plugin.dataManager.isPlayerRegistered(player.uniqueId.toString())) {
            player.sendMessage(plugin.getMessage("messages.prompt-login"))
        } else {
            player.sendMessage(plugin.getMessage("messages.prompt-register"))
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        plugin.setAuthenticated(event.player.uniqueId, false)
    }

    private fun isNotAuthenticated(player: Player) = !plugin.isAuthenticated(player.uniqueId)

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (isNotAuthenticated(event.player)) {
            val from: Location = event.from
            val to: Location? = event.to
            if (to != null && (from.x != to.x || from.z != to.z)) {
                event.isCancelled = true
                from.pitch = to.pitch
                from.yaw = to.yaw
                event.player.teleport(from)
            }
        }
    }

    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        if (isNotAuthenticated(event.player)) {
            event.isCancelled = true
            event.player.sendMessage(plugin.getMessage("messages.must-be-logged-in-chat"))
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (isNotAuthenticated(event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (isNotAuthenticated(event.player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onCommandPreprocess(event: PlayerCommandPreprocessEvent) {
        if (isNotAuthenticated(event.player)) {
            val command = event.message.split(" ")[0].lowercase()
            if (command != "/register" && command != "/login" && command !="/resetpassword") {
                event.isCancelled = true
                event.player.sendMessage(plugin.getMessage("messages.must-be-logged-in-commands"))
            }
        }
    }
}