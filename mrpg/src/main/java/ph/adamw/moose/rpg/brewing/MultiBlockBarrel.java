package ph.adamw.moose.rpg.brewing;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ph.adamw.moose.core.MCore;
import ph.adamw.moose.core.util.ItemUtils;
import ph.adamw.moose.core.util.multiblock.MultiBlock;
import ph.adamw.moose.core.util.multiblock.pattern.MultiBlockPattern;
import ph.adamw.moose.core.util.multiblock.pattern.MultiBlockStairs;
import ph.adamw.moose.survival.MSurvival;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MultiBlockBarrel extends MultiBlock implements Listener {
	private transient Inventory openInventory;

	private List<ItemStack> inventory = new ArrayList<>();
	private long lastAccess;

	private static transient MultiBlockPattern PATTERN = new MultiBlockPattern(
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

			/*
			// Bottom
			new MultiBlockCore(Material.OAK_PLANKS),
			new MultiBlockElement(Material.OAK_PLANKS, 1, 0, 0), //
			new MultiBlockElement(Material.OAK_PLANKS, 1, 0, 1), //
			new MultiBlockElement(Material.OAK_PLANKS, 0, 0, 1), //

			// Top
			new MultiBlockElement(Material.OAK_PLANKS, 0, 1, 0),
			new MultiBlockElement(Material.OAK_PLANKS, 1, 1, 0),
			new MultiBlockElement(Material.OAK_PLANKS, 1, 1, 1),
			new MultiBlockElement(Material.OAK_PLANKS, 0, 1, 1)
			*/
		);

	@Override
	public MultiBlockPattern getPattern() {
		return PATTERN;
	}

	@Override
	public String getName() {
		return "barrel";
	}

	@Override
	public boolean isTrusted(Player player) {
		return true;
	}

	@Override
	public void onCreate(Player player) {
		MSurvival.getPlugin().getServer().getPluginManager().registerEvents(this, MCore.getPlugin());
	}

	@Override
	public void onActivate(Player player) {
		if(openInventory == null) {
			openInventory = Bukkit.createInventory(player, 9, "Fermenting Barrel");
			ItemUtils.copyInventoryConents(inventory, openInventory);
		}

		player.openInventory(openInventory);
	}

	@Override
	public void onDestroy(Player player) {
		//TODO drop all items on player
	}

	@EventHandler
	public void onInventoryClosed(InventoryCloseEvent event) {
		if(openInventory == null) {
			return;
		}

		if(event.getInventory().equals(openInventory)) {
			inventory.clear();
			inventory.addAll(Arrays.asList(openInventory.getContents()));

			if(openInventory.getViewers().size() == 1) {
				openInventory = null;
				lastAccess = new Date().getTime();
			}
		}
	}
}
