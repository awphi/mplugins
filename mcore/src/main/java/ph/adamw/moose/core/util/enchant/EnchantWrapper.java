package ph.adamw.moose.core.util.enchant;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.plugin.java.JavaPlugin;
import ph.adamw.moose.core.MCore;

import java.lang.reflect.Field;
import java.util.concurrent.ThreadLocalRandom;

public abstract class EnchantWrapper extends Enchantment {
	public EnchantWrapper(JavaPlugin plugin, String namespace) {
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

	public abstract boolean isAvailableOnTable();

	public abstract float getTableChance();

	public abstract int getTableLevel();
}
