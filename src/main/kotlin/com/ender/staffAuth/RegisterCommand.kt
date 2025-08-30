package com.ender.staffAuth

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class RegisterCommand(private val plugin: StaffAuth) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) { return true }

        val uuid = sender.uniqueId.toString()

        if (plugin.dataManager.isPlayerRegistered(uuid)) {
            sender.sendMessage(plugin.getMessage("messages.already-registered"))
            return true
        }
        if (args.size != 2) { return false }

        val password = args[0]
        if (password != args[1]) {
            sender.sendMessage(plugin.getMessage("messages.passwords-do-not-match"))
            return true
        }

        val ip = sender.address?.address?.hostAddress ?: "unknown"
        val ipHash = PasswordHashing.hashIp(ip, uuid)
        val hashedPassword = PasswordHashing.hashPassword(password)

        plugin.dataManager.registerPlayer(uuid, hashedPassword, ipHash)
        plugin.setAuthenticated(sender.uniqueId, true)
        sender.sendMessage(plugin.getMessage("messages.register-success"))

        return true
    }
}