package ph.adamw.moose.core.util;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public abstract class PluginEnchantmentWrapper extends Enchantment {
	public PluginEnchantmentWrapper(JavaPlugin plugin, String namespace) {
		super(new NamespacedKey(plugin, namespace));
		registerEnchantment(this);
	}

	public static void registerEnchantment(Enchantment enchantment) {
		try {
			Field f = Enchantment.class.getDeclaredField("acceptingNew");
			f.setAccessible(true);
			f.set(null, true);
			Enchantment.registerEnchantment(enchantment);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
