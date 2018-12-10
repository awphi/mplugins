package ph.adamw.moose.rpg.brewing.multiblock;

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
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.util.Vector;
import ph.adamw.moose.core.util.chat.ChatUtils;
import ph.adamw.moose.core.util.multiblock.MultiBlock;
import ph.adamw.moose.core.util.multiblock.pattern.MultiBlockCore;
import ph.adamw.moose.core.util.multiblock.pattern.MultiBlockElement;
import ph.adamw.moose.core.util.multiblock.pattern.MultiBlockPattern;
import ph.adamw.moose.rpg.brewing.BrewRecipe;
import ph.adamw.moose.rpg.brewing.BrewRegistry;
import ph.adamw.moose.rpg.brewing.BrewHandler;

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
	private List<ItemStack> inventory = new ArrayList<>();

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
	public void onCreate(Player player) {

	}

	@Override
	public void onActivate(PlayerInteractEvent event) {
		final Block cauldron = getCoreLocation().getWorld().getBlockAt(getCoreLocation());
		final ItemStack itemInHand = event.getPlayer().getInventory().getItemInMainHand();

		// Create a new mixture
		if(inventory.size() <= 0) {
			if(itemInHand.getType().equals(Material.WATER_BUCKET)) {
				return;
			}

			final Levelled levelled = (Levelled) cauldron.getBlockData();

			if (levelled.getLevel() != levelled.getMaximumLevel()) {
				ChatUtils.messageInfo(event.getPlayer(), "", "Try filling up the vat and adding some ingredients before you start cooking!");
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

			// Sort the recipe
			inventory.sort((o1, o2) -> {
				if (o1.getAmount() == o2.getAmount()) {
					return 0;
				}

				return o1.getAmount() < o2.getAmount() ? 1 : -1;
			});

			startTime = Instant.now().getEpochSecond();
			ChatUtils.messageInfo(event.getPlayer(), "", "Your mixture has started cooking.");

			return;
		}

		final long cookTime = Instant.now().getEpochSecond() - startTime;

		// Allow players to check cook time in seconds
		if(itemInHand.getType().equals(Material.CLOCK)) {
			ChatUtils.messageInfo(event.getPlayer(), "", "This mixture has been cooking for {" + cookTime + " seconds}.");
			return;
		}

		if(!itemInHand.getType().equals(Material.GLASS_BOTTLE)) {
			return;
		}

		// Stop regular water being extracted
		event.setCancelled(true);

		final BrewRecipe closestRecipe = BrewRegistry.getClosestRecipe(inventory);
		final double recipeAccuracy = BrewRegistry.getRecipeAccuracy(closestRecipe, inventory);

		// Cook rating 0->1 like other ratings
		final double cookRating = BrewHandler.calculateRating(cookTime - closestRecipe.getCookTime(), closestRecipe.getCookTime(), closestRecipe.getDifficulty());

		final ItemStack item;

		if(closestRecipe.getAgeTime() == -1) {
			// Creates the completed brew
			item = BrewHandler.createBrew(closestRecipe, -1, recipeAccuracy, cookRating);
		} else {
			// Creates the mixture potion to be aged
			final NBTItem mixture = new NBTItem(new ItemStack(Material.POTION, 1));

			final PotionMeta meta = (PotionMeta) mixture.getItem().getItemMeta();
			meta.setColor(Color.OLIVE);
			meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

			final String str = inventory.get(0).getType().name().split("_")[0].toLowerCase().trim();
			final String name = str.substring(0, 1).toUpperCase() + str.substring(1);
			meta.setDisplayName(ChatColor.GRAY + name + "y Mixture");
			meta.setLore(new ArrayList<>(Collections.singleton(ChatColor.GRAY + "Mixed on: " + ChatColor.WHITE + new Date().toString())));

			mixture.getItem().setItemMeta(meta);

			mixture.setString(BrewHandler.CLOSEST_RECIPE, closestRecipe.getName());
			mixture.setDouble(BrewHandler.INGREDIENTS_RATING, recipeAccuracy);
			mixture.setDouble(BrewHandler.COOK_RATING, cookRating);

			item = mixture.getItem();
		}

		// Drop/give the brew + take away bottle
		if(event.getPlayer().getInventory().firstEmpty() != -1) {
			event.getPlayer().getInventory().addItem(item);
		} else {
			getCoreLocation().getWorld().dropItemNaturally(getCoreLocation().add(new Vector(0, 1, 0)), item);
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
