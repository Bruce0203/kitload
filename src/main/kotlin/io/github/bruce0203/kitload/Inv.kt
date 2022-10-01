package io.github.bruce0203.kitload

import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory

fun Plugin.getKit(name: String): Inventory {
    return getInventory(config.getConfigurationSection(name)
        ?: throw AssertionError("inventory not exist"))
}

fun Plugin.getKitList(): MutableSet<String> {
    return config.getKeys(false)
}

fun Plugin.hasKit(name: String): Boolean {
    return config.isSet(name)
}

fun Plugin.saveKit(name: String, inventory: Inventory) {
    inventory.contents.forEachIndexed { index, item ->
        config.set("$name.$index", item)
    }
}

fun getInventory(conf: ConfigurationSection): Inventory {
    val inv = Bukkit.createInventory(null, InventoryType.PLAYER)
    conf.getKeys(false).forEach {
        inv.setItem(it.toInt(), conf.getItemStack(it))
    }
    return inv
}