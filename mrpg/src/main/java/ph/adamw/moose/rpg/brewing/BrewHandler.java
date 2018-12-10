package ph.adamw.moose.rpg.brewing;

import de.tr7zw.itemnbtapi.NBTItem;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import ph.adamw.moose.core.util.config.Config;
import ph.adamw.moose.rpg.MRpg;
import ph.adamw.moose.rpg.brewing.effect.BrewEffect;
import ph.adamw.moose.rpg.brewing.effect.BrewEffectDrunk;
import ph.adamw.moose.rpg.brewing.effect.BrewEffectListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

public class BrewHandler {
	public static final String AGE_RATING = "ageTimeRating";
	public static final String COOK_RATING = "cookTimeRating";
	public static final String INGREDIENTS_RATING = "ingredientsRating";
	public static final String CLOSEST_RECIPE = "closestRecipe";
	public static final String AGE = "age";

	private final Config brewsConfig = new Config(MRpg.getPlugin(), "brews.yml", "brews.yml", false);

	@Getter
	private final BrewRegistry registry = new BrewRegistry();

	static {
		// Register the custom effects
		BrewRegistry.registerCustomEffect(new BrewEffect("FOOD") {
			@Override
			public void run(Player player, int potency, int length) {
				player.setFoodLevel(player.getFoodLevel() + potency / 2);
				player.setSaturation(player.getSaturation() + length);
			}
		});

		BrewRegistry.registerCustomEffect(new BrewEffectDrunk());

		BrewRegistry.registerCustomEffect(new BrewEffect("SELFIGNITION") {
			@Override
			public void run(Player player, int potency, int length) {
				//TODO
			}
		});

		BrewRegistry.registerCustomEffect(new BrewEffect("IGNITION") {
			@Override
			public void run(Player player, int potency, int length) {
				//TODO
			}
		});

		BrewRegistry.registerCustomEffect(new BrewEffect("RAGE") {
			@Override
			public void run(Player player, int potency, int length) {
				//TODO
			}
		});
	}

	public BrewHandler() {
		MRpg.getPlugin().getServer().getPluginManager().registerEvents(new BrewEffectListener(), MRpg.getPlugin());

		BrewRegistry.registerBrew(BrewRecipe.NULL_RECIPE);
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

			final List<String> effects = new ArrayList<>();
			for(String j : section.getStringList("effects")) {
				if(BrewEffect.isValidEffectString(j)) {
					effects.add(j);
				} else {
					MRpg.getPlugin().getLogger().log(Level.SEVERE, "Could not resolve effect: " + j + " in recipe " + i + ", skipping...");
				}
			}

			final BrewRecipe recipe = new BrewRecipe(
					i,
					section.getString("flavourtext"),
					Color.fromRGB(section.getInt("colour")),
					section.getInt("cooktime"),
					section.getInt("agetime"),
					section.getDouble("difficulty"),
					items,
					effects
			);

			BrewRegistry.registerBrew(recipe);
		}
	}

	private static String getRatingString(double rating) {
		int c = 5;
		double rounded = 0.2d * (Math.round(rating / 0.2d));

		final StringBuilder sb = new StringBuilder();
		sb.append(ChatColor.GREEN);

		while(rounded - 0.2 >= 0) {
			rounded -= 0.2;
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
		final double y = -Math.abs((difficulty / ideal) * Math.abs(actual - ideal)) + 1d;
		return Math.max(0d, y);
	}

	public static ItemStack createBrew(BrewRecipe recipe, int age, double ingredientsRating, double cookRating) {
		final double ageRating = recipe.getAgeTime() == -1 ? 1.0d : calculateRating(age, recipe.getAgeTime(), recipe.getDifficulty());

		final NBTItem result = new NBTItem(new ItemStack(Material.POTION, 1));
		final PotionMeta meta = (PotionMeta) result.getItem().getItemMeta();

		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY + recipe.getFlavourText());
		lore.add(ChatColor.GRAY + "Brewed on: " + ChatColor.WHITE + new Date().toString());
		lore.add(ChatColor.GRAY + "Ingredients: " + getRatingString(ingredientsRating));
		lore.add(ChatColor.GRAY + "Cooking: " + getRatingString(cookRating));

		if(recipe.getAgeTime() != -1) {
			lore.add(ChatColor.GRAY + "Aging: " + getRatingString(ageRating));
		}

		if(recipe == BrewRecipe.NULL_RECIPE) {
			// Only display flavour text + brewed on for a null recipe when aged
			lore = lore.subList(0, 2);
		}

		meta.setLore(lore);
		meta.setColor(recipe.getColor());
		meta.setDisplayName(ChatColor.GOLD + recipe.getName());
		meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);

		result.getItem().setItemMeta(meta);

		result.setDouble(AGE_RATING, ageRating);
		result.setString(CLOSEST_RECIPE, recipe.getName());
		result.setDouble(INGREDIENTS_RATING, ingredientsRating);
		result.setDouble(COOK_RATING, cookRating);
		result.setInteger(AGE, age);

		return result.getItem();
	}
}
