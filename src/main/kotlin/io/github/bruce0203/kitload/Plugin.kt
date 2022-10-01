package io.github.bruce0203.kitload

import io.github.brucefreedy.mccommand.MCCommand
import io.github.brucefreedy.mccommand.player
import org.bukkit.ChatColor.RED
import org.bukkit.plugin.java.JavaPlugin

class Plugin : JavaPlugin() {

    override fun onEnable() {
        loadDefaultConfig()
        MCCommand(this) {
            command("kitload") {
                tab { getKitList() }
                execute {
                    try {

                        val name = args[0]
                        player.inventory.contents = getKit(name).contents
                        player.sendMessage("Kit $name loaded!")
                    } catch (e: Exception) {
                        player.sendMessage("$RED${e.message}")
                    }
                }
            }
            command("kitsave") {
                execute {
                    val name = args[0]
                    saveKit(name, player.inventory)
                    player.sendMessage("Kit $name saved!")
                }
            }
        }
    }

    private fun loadDefaultConfig() {
        config.options().copyDefaults()
        saveDefaultConfig()
    }

}