package ph.adamw.moose.core.util;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
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

	public static List<ItemStack> stackify(List<ItemStack> items) {
		final List<ItemStack> result = new ArrayList<>();

		for (ItemStack i : items) { // iterate through the list of items to 'merge'
			int index = getFirstSimilar(result, i); // get the first similar item, if it exists
			if (index > -1) { // if there is a similar stack, figure out how much to add
				final ItemStack tmp = result.get(index);
				int amt = tmp.getAmount();

				if (amt + i.getAmount() > i.getMaxStackSize()) {
					tmp.setAmount(i.getMaxStackSize());
					final ItemStack dupe = tmp.clone();
					dupe.setAmount(i.getAmount() - (i.getMaxStackSize() + amt));
					result.add(dupe);
				} else {
					tmp.setAmount(amt + i.getAmount());
				}

				result.set(index, tmp);
			} else {
				result.add(i);
			}
		}

		return result;
	}

	private static int getFirstSimilar(List<ItemStack> items, ItemStack input) {
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).isSimilar(input)) {
				return i;
			}
		}
		return -1;
	}
}
