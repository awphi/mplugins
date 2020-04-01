package ph.adamw.moose.rpg.brewing.multiblock;

import de.tr7zw.itemnbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ph.adamw.moose.core.util.ItemUtils;
import ph.adamw.moose.core.util.multiblock.MultiBlock;
import ph.adamw.moose.core.util.multiblock.pattern.MultiBlockPattern;
import ph.adamw.moose.core.util.multiblock.pattern.MultiBlockBisectedDirectional;
import ph.adamw.moose.rpg.MRpg;
import ph.adamw.moose.rpg.brewing.BrewRecipe;
import ph.adamw.moose.rpg.brewing.BrewHandler;
import ph.adamw.moose.rpg.brewing.BrewRegistry;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MultiBlockBarrel extends MultiBlock implements Listener {
	private transient Inventory openInventory;
	private transient BukkitTask task;

	private List<ItemStack> inventory = new ArrayList<>();
	private long lastAccess;

	public MultiBlockBarrel() {
		MRpg.getPlugin().getServer().getPluginManager().registerEvents(this, MRpg.getPlugin());
	}

	public final static transient MultiBlockPattern PATTERN = new MultiBlockPattern(
			// Bottom
			new MultiBlockBisectedDirectional(Material.OAK_STAIRS, 0, 0,0, BlockFace.SOUTH, Bisected.Half.TOP),
			new MultiBlockBisectedDirectional(Material.OAK_STAIRS, 1, 0, 0, BlockFace.SOUTH, Bisected.Half.TOP),
			new MultiBlockBisectedDirectional(Material.OAK_STAIRS, 1, 0, 1, BlockFace.NORTH, Bisected.Half.TOP),
			new MultiBlockBisectedDirectional(Material.OAK_STAIRS, 0, 0, 1, BlockFace.NORTH, Bisected.Half.TOP),

			//Top
			new MultiBlockBisectedDirectional(Material.OAK_STAIRS, 0, 1, 0, BlockFace.SOUTH, Bisected.Half.BOTTOM),
			new MultiBlockBisectedDirectional(Material.OAK_STAIRS, 1, 1, 0, BlockFace.SOUTH, Bisected.Half.BOTTOM),
			new MultiBlockBisectedDirectional(Material.OAK_STAIRS, 1, 1, 1, BlockFace.NORTH, Bisected.Half.BOTTOM),
			new MultiBlockBisectedDirectional(Material.OAK_STAIRS, 0, 1, 1, BlockFace.NORTH, Bisected.Half.BOTTOM)
		);

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

	}

	@Override
	public void onActivate(PlayerInteractEvent event) {
		// Performed on first load since last restart
		if(openInventory == null) {
			openInventory = Bukkit.createInventory(event.getPlayer(), 9, "Fermentation Barrel");
			ItemUtils.copyInventoryConents(inventory, openInventory);
		}

		ageBrews();
		event.getPlayer().openInventory(openInventory);

		if(task == null) {
			// Every 3 seconds (60 ticks)
			task = new BukkitRunnable() {
				@Override
				public void run() {
					ageBrews();
					lastAccess = Instant.now().getEpochSecond();
				}
			}.runTaskTimer(MRpg.getPlugin(), 60L, 60L);
		}
	}

	@Override
	public void onDestroy() {
		for(ItemStack i : inventory) {
			if(i != null) {
				getCoreLocation().getWorld().dropItemNaturally(getCoreLocation().add(0d, 2d, 0d), i);
			}
		}
	}

	private void ageBrews() {
		final ItemStack[] stacks = openInventory.getStorageContents();

		for(int i = 0; i < stacks.length; i ++) {
			if(stacks[i] != null && stacks[i].getType() != Material.AIR) {
				final NBTItem item = new NBTItem(stacks[i]);
				if(!item.hasKey(BrewHandler.CLOSEST_RECIPE)) {
					continue;
				}

				final BrewRecipe recipe = BrewRegistry.getBrew(item.getString(BrewHandler.CLOSEST_RECIPE));
				final double cookRating = item.getDouble(BrewHandler.COOK_RATING);
				final double ingredientsRating = item.getDouble(BrewHandler.INGREDIENTS_RATING);

				int age = (int) Math.abs(Instant.now().getEpochSecond() - lastAccess);

				if(item.hasKey(BrewHandler.AGE)) {
					age += item.getInteger(BrewHandler.AGE);
				}

				openInventory.setItem(i, BrewHandler.createBrew(recipe, age, ingredientsRating, cookRating));
			}
		}

	}

	public void onInventoryClosed(InventoryCloseEvent event) {
		if(openInventory == null) {
			return;
		}

		if(openInventory.equals(event.getInventory())) {
			inventory.clear();
			inventory.addAll(Arrays.asList(openInventory.getContents()));

			lastAccess = Instant.now().getEpochSecond();

			if(openInventory.getViewers().size() == 1) {
				task.cancel();
				task = null;
			}
		}
	}

	public static MultiBlockBarrel deserialize(Map<String, Object> map) {
		return deserializeBase(MultiBlockBarrel.class, map);
	}
}
