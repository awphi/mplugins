package ph.adamw.moose.eco;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ph.adamw.moose.core.util.AutoSerializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EconomyProfile extends AutoSerializable implements Listener {
	@Getter
	private double balance = 0;

	@Setter
	private int vaultSize = 9;

	private List<ItemStack> vault = new ArrayList<>();

	private transient Inventory openVault;

	public EconomyProfile() {
		MEconomy.getPlugin().getServer().getPluginManager().registerEvents(this, MEconomy.getPlugin());
	}

	public EconomyProfile(Map<String, Object> map) {
		super(map);
	}

	public void addBalance(int add) {
		balance += add;
	}

	public void removeBalance(int remove) {
		balance = balance - remove >= 0 ? balance - remove : 0;
	}

	public void openVault(Player to) {
		if(openVault == null) {
			openVault = Bukkit.createInventory(to, vaultSize, "Vault");
			openVault.addItem(vault.toArray(new ItemStack[0]));
		}

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

			openVault = null;
		}
	}
}
