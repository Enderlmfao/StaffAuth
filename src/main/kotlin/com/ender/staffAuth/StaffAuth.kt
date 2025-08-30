package com.ender.staffAuth

import net.md_5.bungee.api.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class StaffAuth : JavaPlugin() {

    lateinit var dataManager: DataManager
        private set

    val authenticatedPlayers = mutableSetOf<UUID>()

    override fun onEnable() {
        saveDefaultConfig()

        dataManager = DataManager(this)
        dataManager.loadDatabase()

        getCommand("register")?.setExecutor(RegisterCommand(this))
        getCommand("login")?.setExecutor(LoginCommand(this))
        getCommand("resetpassword")?.setExecutor(ResetPasswordCommand(this))

        server.pluginManager.registerEvents(PlayerListener(this), this)
        logger.info("StaffAuth has been enabled!")
    }

    override fun onDisable() {
        dataManager.closeConnection()
        logger.info("StaffAuth has been disabled!")
    }

    fun isAuthenticated(playerUuid: UUID): Boolean {
        return authenticatedPlayers.contains(playerUuid)
    }

    fun setAuthenticated(playerUuid: UUID, authenticated: Boolean) {
        if (authenticated) authenticatedPlayers.add(playerUuid) else authenticatedPlayers.remove(playerUuid)
    }

    fun getMessage(path: String): String {
        val message = config.getString(path) ?: "&cMessage not found: $path"
        return ChatColor.translateAlternateColorCodes('&', message)
    }
}