package ph.adamw.moose.rpg.brewing;

import de.tr7zw.itemnbtapi.NBTItem;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import ph.adamw.moose.core.util.config.Config;
import ph.adamw.moose.rpg.MRpg;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

public class BrewingHandler {
	public static final String AGE_RATING = "ageTimeRating";
	public static final String COOK_RATING = "cookTimeRating";
	public static final String INGREDIENTS_RATING = "ingredientsRating";
	public static final String CLOSEST_RECIPE = "closestRecipe";

	private final Config brewsConfig = new Config(MRpg.getPlugin(), "brews.yml", "brews.yml", false);

	@Getter
	private final BrewRegistry registry = new BrewRegistry();

	public BrewingHandler() {
		registry.register(BrewRecipe.NULL_RECIPE);
		// Parses the config
		for(String i : brewsConfig.getConfigurationSection("brews").getKeys(false)) {
			final ConfigurationSection section = brewsConfig.getConfigurationSection("brews").getConfigurationSection(i);
			final List<ItemStack> items = new ArrayList<>();

			for(String j : section.getStringList("ingredients")) {
				final String[] split = j.trim().split(" ");
				split[1] = split[1].toUpperCase();

				if(Material.getMaterial(split[1]) == null) {
					MRpg.getPlugin().getLogger().log(Level.SEVERE, "Could not resolve material: " + split[1] + " in recipe " + i + ", skipping...");
					continue;
				}

				items.add(new ItemStack(Material.getMaterial(split[1]), Integer.valueOf(split[0])));
			}

			// TODO potion effects + custom effects parsed and loaded
			final BrewRecipe recipe = new BrewRecipe(
					i,
					section.getString("flavourtext"),
					section.getColor("colour"),
					section.getInt("cooktime"),
					section.getInt("agetime"),
					section.getDouble("difficulty"),
					items
			);

			registry.register(recipe);
		}
	}

	private String getRatingString(double rating) {
		final StringBuilder sb = new StringBuilder();
		int c = 5;
		sb.append(ChatColor.GREEN);

		while(rating - 0.2 >= 0) {
			rating -= 0.2;
			sb.append("■");
			c --;
		}

		sb.append(ChatColor.DARK_GRAY);
		for(int i = 0; i < c; i ++) {
			sb.append("■");
		}

		return sb.toString();
	}

	public static double calculateRating(double actual, double ideal, double difficulty) {
		// Follows function: y = -|d/m * (x - m)| + 1 : d = difficulty, m = ideal, x = actual
		// Use: https://www.desmos.com/calculator/dyalicei6u to play around with this function
		return -Math.abs((difficulty / ideal) * (actual - ideal)) + 1;
	}

	public ItemStack createBrew(BrewRecipe recipe, long lastCheck, double ingredientsRating, double cookRating) {
		final NBTItem result = new NBTItem(new ItemStack(Material.POTION, 1));
		final PotionMeta meta = (PotionMeta) result.getItem().getItemMeta();
		meta.setColor(recipe.getColor());
		meta.setDisplayName(ChatColor.GOLD + recipe.getName());

		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY + recipe.getFlavourText());
		lore.add(ChatColor.GRAY + "Brewed on: " + ChatColor.WHITE + new Date().toString());
		lore.add(ChatColor.GRAY + "Ingredients: " + getRatingString(ingredientsRating));
		lore.add(ChatColor.GRAY + "Cooking: " + getRatingString(cookRating));

		if(recipe.getAgeTime() == -1) {
			result.setDouble(AGE_RATING, 1.0d);
			lore.add(ChatColor.GRAY + "Aging: " + getRatingString(1.0d));
		} else {
			final double rating = calculateRating(Instant.now().getEpochSecond() - lastCheck, recipe.getCookTime(), recipe.getDifficulty());
			result.setDouble(AGE_RATING, rating);
			lore.add(ChatColor.GRAY + "Aging: " + getRatingString(rating));
		}

		if(recipe == BrewRecipe.NULL_RECIPE) {
			// Only display flavour text + brewed on for a null recipe when aged
			lore = lore.subList(0, 2);
		}

		meta.setLore(lore);

		result.setInteger("HideFlags", 32);
		result.setString(CLOSEST_RECIPE, recipe.getName());
		result.setDouble(INGREDIENTS_RATING, ingredientsRating);
		result.setDouble(COOK_RATING, cookRating);

		result.getItem().setItemMeta(meta);

		return result.getItem();
	}
}
