package ph.adamw.moose.core.util;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemUtils {
	public static void copyInventoryConents(List<ItemStack> items, Inventory inventory) {
		for(int i = 0; i < inventory.getSize(); i ++) {
			if(i >= items.size()) {
				break;
			}

			final ItemStack is = items.get(i);

			if(is != null) {
				inventory.setItem(i, is);
			}
		}
	}
}
