package com.ender.staffAuth

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ResetPasswordCommand(private val plugin: StaffAuth) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) { return true }

        val uuid = sender.uniqueId.toString()

        if (!plugin.dataManager.isPlayerRegistered(uuid)) {
            sender.sendMessage(plugin.getMessage("messages.reset-not-registered"))
            return true
        }

        if (args.size != 1) { return false }

        val ip = sender.address?.address?.hostAddress ?: "unknown"
        val ipHash = PasswordHashing.hashIp(ip, uuid)

        if (plugin.dataManager.isKnownIp(uuid, ipHash)) {
            val newPassword = args[0]
            val newHashedPassword = PasswordHashing.hashPassword(newPassword)
            plugin.dataManager.updatePassword(uuid, newHashedPassword)
            plugin.setAuthenticated(sender.uniqueId, false)
            sender.sendMessage(plugin.getMessage("messages.reset-success"))
        } else {
            sender.sendMessage(plugin.getMessage("messages.reset-ip-not-recognized"))
        }
        return true
    }
}