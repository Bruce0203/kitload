package io.github.bruce0203.kitload

import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory


fun Plugin.getKit(name: String): Inventory {
    return getInventory(kitConf.getConfigurationSection(name)
        ?: throw AssertionError("inventory not exist"))
}

fun Plugin.getKitList(): MutableSet<String> {
    return kitConf.getKeys(false)
}

@Suppress("unused")
fun Plugin.hasKit(name: String): Boolean {
    return kitConf.isSet(name)
}

fun Plugin.deleteKit(name: String) {
    kitConf.set(name, null)
    saveKit()
}

fun Plugin.saveKit(name: String, inventory: Inventory) {
    inventory.contents.forEachIndexed { index, item ->
        kitConf.set("$name.$index", item)
    }
    saveKit()
}

fun getInventory(conf: ConfigurationSection): Inventory {
    val inv = Bukkit.createInventory(null, InventoryType.PLAYER)
    conf.getKeys(false).forEach {
        inv.setItem(it.toInt(), conf.getItemStack(it))
    }
    return inv
}