package ph.adamw.moose.eco;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EconomyProfile implements ConfigurationSerializable, Listener {
	@Getter
	private double balance = 0;

	public List<ItemStack> vault = new ArrayList<>();
	private Inventory openVault;

	public void addBalance(int add) {
		balance += add;
	}

	public void removeBalance(int remove) {
		balance = balance - remove >= 0 ? balance - remove : 0;
	}

	public void openVault(Player to) {
		openVault = Bukkit.createInventory(to, (int) Math.ceil((double) vault.size() / 9.0d));
		openVault.addItem(vault.toArray(new ItemStack[0]));
		to.openInventory(openVault);
	}

	@EventHandler
	public void onInventoryClosed(InventoryCloseEvent event) {
		if(openVault == null) {
			return;
		}

		if(openVault == event.getInventory()) {
			vault.clear();
			vault.addAll(Arrays.asList(openVault.getContents()));
			openVault.clear();
		}
	}

	@SuppressWarnings("unchecked")
	public static EconomyProfile deserialize(Map<String, Object> map) {
		final EconomyProfile result = new EconomyProfile();
		result.balance = (double) map.get("balance");
		result.vault = (List<ItemStack>) map.get("vault");

		return result;
	}

	@Override
	public Map<String, Object> serialize() {
		final Map<String, Object> result = new LinkedHashMap<>();
		result.put("balance", balance);
		result.put("vault", vault);

		return result;
	}
}
