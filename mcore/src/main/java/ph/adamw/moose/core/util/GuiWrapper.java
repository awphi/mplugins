package ph.adamw.moose.core.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GuiWrapper implements Listener {
	// Create a new inventory, with no owner, a size of nine, called example
	private final Inventory inventory;
	private final Map<ItemStack, Runnable> actionMap = new HashMap<>();

	public GuiWrapper(int size, String name) {
		inventory = Bukkit.createInventory(null, size, name);
	}

	private static ItemStack createGuiItem(String name, ArrayList<String> desc, Material mat) {
		ItemStack i = new ItemStack(mat, 1);
		ItemMeta iMeta = i.getItemMeta();
		iMeta.setDisplayName(name);
		iMeta.setLore(desc);
		i.setItemMeta(iMeta);
		return i;
	}

	public ItemStack addItem(int slot, String name, ArrayList<String> desc, Material mat, Runnable onClick) {
		if(onClick == null) {
			onClick = () -> {};
		}

		final ItemStack is = createGuiItem(name, desc, mat);
		actionMap.put(is, onClick);
		inventory.setItem(slot, is);

		return is;
	}

	public void removeItem(ItemStack itemStack) {
		inventory.remove(itemStack);
	}

	public void removeItem(int slot) {
		inventory.remove(inventory.getItem(slot));
	}


	public void openGui(Player p) {
		p.openInventory(inventory);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		final String invName = e.getInventory().getName();

		if (!invName.equals(inventory.getName())) {
			return;
		}

		if (e.getClick().equals(ClickType.NUMBER_KEY)){
			e.setCancelled(true);
		}

		e.setCancelled(true);

		final ItemStack clicked = e.getCurrentItem();

		// verify current item is not null
		if (clicked == null || clicked.getType().equals(Material.AIR)) {
			return;
		}

		actionMap.get(clicked).run();
	}
}
