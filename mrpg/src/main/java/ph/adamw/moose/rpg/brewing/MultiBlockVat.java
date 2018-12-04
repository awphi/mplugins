package ph.adamw.moose.rpg.brewing;

import de.tr7zw.itemnbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.material.Cauldron;
import ph.adamw.moose.core.util.ItemUtils;
import ph.adamw.moose.core.util.chat.ChatUtils;
import ph.adamw.moose.core.util.multiblock.MultiBlock;
import ph.adamw.moose.core.util.multiblock.pattern.MultiBlockCore;
import ph.adamw.moose.core.util.multiblock.pattern.MultiBlockElement;
import ph.adamw.moose.core.util.multiblock.pattern.MultiBlockPattern;
import ph.adamw.moose.rpg.MRpg;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MultiBlockVat extends MultiBlock {
	private final static transient MultiBlockPattern PATTERN = new MultiBlockPattern(
			new MultiBlockCore(Material.CAULDRON),
			new MultiBlockElement(Material.FIRE, 0 , -1, 0)
	);

	private final static transient double CAULDRON_INTERNAL_RANGE = 0.75d;

	private long startTime;
	private BrewRecipe closestRecipe;
	private List<ItemStack> inventory = new ArrayList<>();;
	private double recipeAccuracy;

	@Override
	public MultiBlockPattern getPattern() {
		return PATTERN;
	}

	@Override
	public String getName() {
		return "brewing vat";
	}

	@Override
	public boolean isTrusted(Player player) {
		return true;
	}

	@Override
	public void onCreate(Player player) {}

	@Override
	public void onActivate(PlayerInteractEvent event) {
		final Block cauldron = getCoreLocation().getWorld().getBlockAt(getCoreLocation());

		// Create a new mixture
		if(inventory.size() <= 0) {
			if (((Cauldron) cauldron.getState().getData()).isFull()) {
				return;
			}

			final Collection<Entity> items = getCoreLocation().getWorld().getNearbyEntities(getCoreLocation(), CAULDRON_INTERNAL_RANGE, CAULDRON_INTERNAL_RANGE, CAULDRON_INTERNAL_RANGE);
			items.removeIf(entity -> !(entity instanceof Item));

			if (items.size() <= 0) {
				return;
			}

			// Delete entities + add them to recipe
			for (Entity i : items) {
				inventory.add(((Item) i).getItemStack());
				i.remove();
			}

			// Flatten the recipe
			inventory = ItemUtils.stackify(inventory);

			// Sort the recipe
			inventory.sort((o1, o2) -> {
				if (o1.getAmount() == o2.getAmount()) {
					return 0;
				}

				return o1.getAmount() > o2.getAmount() ? 1 : -1;
			});

			// Set up all necessary data - CLOSEST RECIPE CAN BE NULL
			startTime = Instant.now().getEpochSecond();
			closestRecipe = MRpg.getPlugin().getBrewingHandler().getRegistry().getClosestRecipe(inventory);
			recipeAccuracy = MRpg.getPlugin().getBrewingHandler().getRegistry().getRecipeAccuracy(closestRecipe, inventory);

			return;
		}

		final long cookTime = Instant.now().getEpochSecond() - startTime;

		// Allow players to check cook time in seconds
		if(event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.CLOCK)) {
			ChatUtils.messageInfo(event.getPlayer(), "Hot hot hot!", "This mixture has been cooking for {" + cookTime + " seconds}.");
			return;
		}

		if(!event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.GLASS_BOTTLE)) {
			return;
		}

		// Stop regular water being extracted
		event.setCancelled(true);

		// Cook rating 0->1 like other ratings
		final double cookRating = BrewingHandler.calculateRating(cookTime - closestRecipe.getCookTime(), closestRecipe.getCookTime(), closestRecipe.getDifficulty());

		final ItemStack brew;

		if(closestRecipe.getCookTime() == -1) {
			// Creates the completed brew
			brew = MRpg.getPlugin().getBrewingHandler().createBrew(closestRecipe, 1L, recipeAccuracy, cookRating);
		} else {
			// Creates the mixture potion to be aged
			final NBTItem vial = new NBTItem(new ItemStack(Material.POTION, 1));
			final PotionMeta meta = (PotionMeta) vial.getItem().getItemMeta();
			meta.setColor(Color.OLIVE);
			meta.setDisplayName(ChatColor.GRAY +  inventory.get(0).getType().name() + "y Mixture");
			meta.setLore(new ArrayList<>(Collections.singleton(ChatColor.GRAY + "Brewed on: " + ChatColor.WHITE + new Date().toString())));

			vial.setInteger("HideFlags", 32);
			vial.setString(BrewingHandler.CLOSEST_RECIPE, closestRecipe.getName());
			vial.setDouble(BrewingHandler.INGREDIENTS_RATING, recipeAccuracy);
			vial.setDouble(BrewingHandler.COOK_RATING, cookRating);

			vial.getItem().setItemMeta(meta);
			brew = vial.getItem();
		}

		// Drop/give the brew + take away bottle
		if(event.getPlayer().getInventory().contains(Material.AIR)) {
			event.getPlayer().getInventory().addItem(brew);
		} else {
			getCoreLocation().getWorld().dropItem(getCoreLocation(), brew);
		}

		event.getPlayer().getInventory().remove(new ItemStack(Material.GLASS_BOTTLE, 1));

		// Decrease cauldron level and clear multiblock data ready for next mixture when empty
		final Levelled cauldronData = (Levelled) cauldron.getBlockData();
		cauldronData.setLevel(cauldronData.getLevel() - 1);
		cauldron.setBlockData(cauldronData);

		if(cauldronData.getLevel() == 0) {
			inventory.clear();
		}
	}

	@Override
	public void onDestroy() {}

	public static MultiBlockVat deserialize(Map<String, Object> map) {
		return deserializeBase(MultiBlockVat.class, map);
	}
}
