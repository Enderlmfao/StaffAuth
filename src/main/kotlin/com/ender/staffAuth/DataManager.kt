package com.ender.staffAuth

import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DataManager(private val plugin: StaffAuth) {

    private var connection: Connection? = null

    fun loadDatabase() {
        val dataFolder = plugin.dataFolder.apply { mkdirs() }
        val dbFile = File(dataFolder, "auth.db")
        try {
            Class.forName("org.sqlite.JDBC")
            connection = DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}")
            connection?.createStatement()?.use { statement ->
                statement.execute("CREATE TABLE IF NOT EXISTS auth (uuid TEXT PRIMARY KEY, password TEXT, known_ips TEXT);") // Ensure table has all columns
            }
            if (!doesColumnExist("auth", "known_ips")) {
                plugin.logger.info("Adding 'known_ips' column to the database...")
                connection?.createStatement()?.use { it.execute("ALTER TABLE auth ADD COLUMN known_ips TEXT;") }
            }
        } catch (e: Exception) {
            plugin.logger.severe("Could not initialize database!")
            e.printStackTrace()
        }
    }

    private fun doesColumnExist(tableName: String, columnName: String): Boolean {
        connection?.metaData?.getColumns(null, null, tableName, columnName)?.use {
            return it.next()
        }
        return false
    }

    fun closeConnection() {
        try {
            connection?.takeIf { !it.isClosed }?.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun isPlayerRegistered(uuid: String): Boolean {
        val sql = "SELECT password FROM auth WHERE uuid = ?;"
        return try {
            connection?.prepareStatement(sql)?.use { ps ->
                ps.setString(1, uuid)
                ps.executeQuery().use { it.next() }
            } ?: false
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    fun registerPlayer(uuid: String, hashedPassword: String, ipHash: String) {
        val sql = "INSERT INTO auth (uuid, password, known_ips) VALUES (?, ?, ?);"
        try {
            connection?.prepareStatement(sql)?.use { ps ->
                ps.setString(1, uuid)
                ps.setString(2, hashedPassword)
                ps.setString(3, ipHash)
                ps.executeUpdate()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun getPassword(uuid: String): String? {
        val sql = "SELECT password FROM auth WHERE uuid = ?;"
        return try {
            connection?.prepareStatement(sql)?.use { ps ->
                ps.setString(1, uuid)
                ps.executeQuery().use { rs ->
                    if (rs.next()) rs.getString("password") else null
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
            null
        }
    }

    fun updatePassword(uuid: String, newHashedPassword: String) {
        val sql = "UPDATE auth SET password = ? WHERE uuid = ?;"
        try {
            connection?.prepareStatement(sql)?.use { ps ->
                ps.setString(1, newHashedPassword)
                ps.setString(2, uuid)
                ps.executeUpdate()
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    fun isKnownIp(uuid: String, ipHash: String): Boolean {
        val sql = "SELECT known_ips FROM auth WHERE uuid = ?;"
        return try {
            connection?.prepareStatement(sql)?.use { ps ->
                ps.setString(1, uuid)
                ps.executeQuery().use { rs ->
                    if (rs.next()) {
                        val ipList = rs.getString("known_ips") ?: ""
                        ipList.split(",").contains(ipHash)
                    } else false
                }
            } ?: false
        } catch (e: SQLException) {
            e.printStackTrace()
            false
        }
    }

    fun addKnownIp(uuid: String, ipHash: String) {
        val getSql = "SELECT known_ips FROM auth WHERE uuid = ?;"
        try {
            connection?.prepareStatement(getSql)?.use { ps ->
                ps.setString(1, uuid)
                ps.executeQuery().use { rs ->
                    if (rs.next()) {
                        val ipListStr = rs.getString("known_ips") ?: ""
                        val ipList = ipListStr.split(",").filter { it.isNotBlank() }.toMutableSet()
                        if (ipList.add(ipHash)) {
                            val newIpListStr = ipList.joinToString(",")
                            val updateSql = "UPDATE auth SET known_ips = ? WHERE uuid = ?;"
                            connection?.prepareStatement(updateSql)?.use { updatePs ->
                                updatePs.setString(1, newIpListStr)
                                updatePs.setString(2, uuid)
                                updatePs.executeUpdate()
                            }
                        }
                    }
                }
            }
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
}