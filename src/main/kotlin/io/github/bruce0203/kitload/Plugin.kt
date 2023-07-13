package io.github.bruce0203.kitload

import io.github.brucefreedy.mccommand.MCCommand
import io.github.brucefreedy.mccommand.player
import org.bukkit.ChatColor
import org.bukkit.ChatColor.RED
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class Plugin : JavaPlugin() {

    private val kitFile = File(dataFolder, "kits.yml").apply { if (!exists()) {
        parentFile.mkdirs()
        createNewFile()
    } }
    var kitConf = YamlConfiguration.loadConfiguration(kitFile)
    val permissionMessage get() = config.getString("permission-message")
        .run { ChatColor.translateAlternateColorCodes('&', this
            ?: throw AssertionError("config permission-message not exists!")) }
    fun saveKit() {
        kitConf.save(kitFile)
    }

    override fun onEnable() {
        loadDefaultConfig()
        MCCommand(this) {
            val kitLoad = "kitload"
            val kitSave = "kitsave"
            val kitRemove = "kitremove"
            val kitReload = "kitreload"
            command(kitLoad) {
                tab { getKitList() }
                execute {
                    if (!hasPermission(kitReload, player)) return@execute
                    try {
                        val name = args[0]
                        player.inventory.contents = getKit(name).contents
                        player.sendMessage("Kit $name loaded!")
                    } catch (e: Exception) {
                        player.sendMessage("$RED${e.message}")
                    }
                }
            }
            command(kitSave) {
                execute {
                    if (!hasPermission(kitReload, player)) return@execute
                    val name = args[0]
                    saveKit(name, player.inventory)
                    player.sendMessage("Kit $name saved!")
                }
            }
            command(kitRemove) {
                tab { getKitList() }
                execute {
                    if (!hasPermission(kitReload, player)) return@execute
                    deleteKit(args[0])
                    player.sendMessage("Kit $name removed!")
                }
            }
            executeCommand(kitReload) {
                if (!hasPermission(kitReload, player)) return@executeCommand
                kitConf = YamlConfiguration.loadConfiguration(kitFile)
                reloadConfig()
                player.sendMessage("Kit reloaded!")
            }
        }
    }

    fun hasPermission(perm: String, player: Player, sendPermissionMessage: Boolean = true): Boolean {
        return when (config.getString("command-permissions.$perm")) {
            "true" -> true
            "op" -> player.isOp
            "not op" -> !player.isOp
            else -> false
        }.apply { if (sendPermissionMessage) player.sendMessage(permissionMessage) }
    }

    private fun loadDefaultConfig() {
        config.options().copyDefaults()
        saveDefaultConfig()
    }

}
