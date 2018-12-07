package ph.adamw.moose.rpg.brewing;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ph.adamw.moose.core.MCore;
import ph.adamw.moose.core.util.ItemUtils;
import ph.adamw.moose.core.util.multiblock.MultiBlock;
import ph.adamw.moose.core.util.multiblock.pattern.MultiBlockPattern;
import ph.adamw.moose.core.util.multiblock.pattern.MultiBlockStairs;
import ph.adamw.moose.rpg.MRpg;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MultiBlockBarrel extends MultiBlock implements Listener {
	private transient Inventory openInventory;

	private List<ItemStack> inventory = new ArrayList<>();

	private final static transient MultiBlockPattern PATTERN = new MultiBlockPattern(
			// Bottom
			new MultiBlockStairs(Material.OAK_STAIRS, 0, 0,0, BlockFace.SOUTH, Bisected.Half.TOP),
			new MultiBlockStairs(Material.OAK_STAIRS, 1, 0, 0, BlockFace.SOUTH, Bisected.Half.TOP),
			new MultiBlockStairs(Material.OAK_STAIRS, 1, 0, 1, BlockFace.NORTH, Bisected.Half.TOP),
			new MultiBlockStairs(Material.OAK_STAIRS, 0, 0, 1, BlockFace.NORTH, Bisected.Half.TOP),

			//Top
			new MultiBlockStairs(Material.OAK_STAIRS, 0, 1, 0, BlockFace.SOUTH, Bisected.Half.BOTTOM),
			new MultiBlockStairs(Material.OAK_STAIRS, 1, 1, 0, BlockFace.SOUTH, Bisected.Half.BOTTOM),
			new MultiBlockStairs(Material.OAK_STAIRS, 1, 1, 1, BlockFace.NORTH, Bisected.Half.BOTTOM),
			new MultiBlockStairs(Material.OAK_STAIRS, 0, 1, 1, BlockFace.NORTH, Bisected.Half.BOTTOM)
		);

	@Override
	public MultiBlockPattern getPattern() {
		return PATTERN;
	}

	@Override
	public String getName() {
		return "fermentation barrel";
	}

	@Override
	public boolean isTrusted(Player player) {
		return true;
	}

	@Override
	public void onCreate(Player player) {
		MRpg.getPlugin().getServer().getPluginManager().registerEvents(this, MCore.getPlugin());
	}

	@Override
	public void onActivate(PlayerInteractEvent event) {
		if(openInventory == null) {
			openInventory = Bukkit.createInventory(event.getPlayer(), 9, "Fermentation Barrel");
			ItemUtils.copyInventoryConents(inventory, openInventory);
		}

		event.getPlayer().openInventory(openInventory);
	}

	@Override
	public void onDestroy() {
		for(ItemStack i : inventory) {
			if(i != null) {
				getCoreLocation().getWorld().dropItemNaturally(getCoreLocation().add(0d, 2d, 0d), i);
			}
		}
	}

	@EventHandler
	public void onInventoryClosed(InventoryCloseEvent event) {
		if(openInventory == null) {
			return;
		}

		if(event.getInventory().equals(openInventory)) {
			inventory.clear();
			inventory.addAll(Arrays.asList(openInventory.getContents()));
		}
	}

	public static MultiBlockBarrel deserialize(Map<String, Object> map) {
		return deserializeBase(MultiBlockBarrel.class, map);
	}
}
