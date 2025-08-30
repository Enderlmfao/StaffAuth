package com.ender.staffAuth

import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.player.*

class PlayerListener(private val plugin: StaffAuth) : Listener {

    private fun isStaff(player: Player) = player.hasPermission("staffauth.use")
    private fun isNotAuthenticated(player: Player) = !plugin.isAuthenticated(player.uniqueId)

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        if (!isStaff(player)) return

        if (plugin.dataManager.isPlayerRegistered(player.uniqueId.toString())) {
            player.sendMessage(plugin.getMessage("messages.prompt-login"))
        } else {
            player.sendMessage(plugin.getMessage("messages.prompt-register"))
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        if (!isStaff(event.player)) return
        plugin.setAuthenticated(event.player.uniqueId, false)
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        if (isStaff(player) && isNotAuthenticated(player)) {
            val from: Location = event.from
            val to: Location? = event.to
            if (to != null && (from.x != to.x || from.z != to.z)) {
                event.isCancelled = true
                from.pitch = to.pitch
                from.yaw = to.yaw
                player.teleport(from)
            }
        }
    }

    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent) {
        val player = event.player
        if (isStaff(player) && isNotAuthenticated(player)) {
            event.isCancelled = true
            player.sendMessage(plugin.getMessage("messages.must-be-logged-in-chat"))
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        if (isStaff(player) && isNotAuthenticated(player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val player = event.player
        if (isStaff(player) && isNotAuthenticated(player)) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onCommandPreprocess(event: PlayerCommandPreprocessEvent) {
        val player = event.player
        if (isStaff(player) && isNotAuthenticated(player)) {
            val command = event.message.split(" ")[0].lowercase()
            if (command != "/register" && command != "/login" && command !="/resetpassword") {
                event.isCancelled = true
                player.sendMessage(plugin.getMessage("messages.must-be-logged-in-commands"))
            }
        }
    }
}