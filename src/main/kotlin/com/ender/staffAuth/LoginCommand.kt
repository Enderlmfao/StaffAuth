package com.ender.staffAuth

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class LoginCommand(private val plugin: StaffAuth) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) { return true }

        if (!sender.hasPermission("staffauth.use")) {
            sender.sendMessage(plugin.getMessage("messages.no-permission"))
            return true
        }

        val uuid = sender.uniqueId.toString()

        if (!plugin.dataManager.isPlayerRegistered(uuid)) {
            sender.sendMessage(plugin.getMessage("messages.not-registered"))
            return true
        }
        if (plugin.isAuthenticated(sender.uniqueId)) {
            sender.sendMessage(plugin.getMessage("messages.already-logged-in"))
            return true
        }
        if (args.size != 1) { return false }

        val password = args[0]
        val hashedPassword = plugin.dataManager.getPassword(uuid)

        if (hashedPassword != null && PasswordHashing.checkPassword(password, hashedPassword)) {
            plugin.setAuthenticated(sender.uniqueId, true)
            sender.sendMessage(plugin.getMessage("messages.login-success"))

            val ip = sender.address?.address?.hostAddress ?: "unknown"
            val ipHash = PasswordHashing.hashIp(ip, uuid)
            plugin.dataManager.addKnownIp(uuid, ipHash)
        } else {
            sender.sendMessage(plugin.getMessage("messages.incorrect-password"))
        }
        return true
    }
}