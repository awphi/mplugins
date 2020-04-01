package ph.adamw.moose.rpg.brewing;

import org.bukkit.inventory.ItemStack;
import ph.adamw.moose.rpg.brewing.effect.BrewEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrewRegistry {
	private static final Map<String, BrewRecipe> recipes = new HashMap<>();
	private static final Map<String, BrewEffect> customEffects = new HashMap<>();

	public static void registerBrew(BrewRecipe recipe) {
		recipes.put(recipe.getName(), recipe);
	}

	public static BrewEffect getCustomEffect(String name) {
		name = name.toUpperCase();

		if(customEffects.containsKey(name)) {
			return customEffects.get(name);
		}

		return null;
	}

	public static void registerCustomEffect(BrewEffect effect) {
		customEffects.put(effect.getName().toUpperCase(), effect);
	}

	public static BrewRecipe getBrew(String name) {
		return recipes.get(name);
	}

	public static BrewRecipe getClosestRecipe(List<ItemStack> items) {
		BrewRecipe scorer = BrewRecipe.NULL_RECIPE;

		// Needs at least a rating of at least 0.2 on any brew to not return null
		double cache = 0.2d;

		for(BrewRecipe i : recipes.values()) {
			double score = rateRecipeAttempt(i, items);

			if(score >= cache && !i.equals(BrewRecipe.NULL_RECIPE)) {
				scorer = i;
				cache = score;
			}
		}

		return scorer;
	}

	public static double getRecipeAccuracy(BrewRecipe closestRecipe, List<ItemStack> items) {
		if(closestRecipe == BrewRecipe.NULL_RECIPE) {
			// Doesn't matter - just stops divide by 0 error
			return 1d;
		}

		return rateRecipeAttempt(closestRecipe, items);
	}

	private static double rateRecipeAttempt(BrewRecipe recipe, List<ItemStack> items) {
		double result = 0d;
		final List<ItemStack> is = new ArrayList<>(items);

		// Adds 1 for a matching item type, removes 1 for a missing item type
		// this punishes people for chucking in everything
		for(ItemStack i : recipe.getIngredients()) {
			ItemStack found = null;

			for(ItemStack j : is) {
				if(j.getType().equals(i.getType())) {
					// For correct item and correct quantity rating respectively
					result ++;
					result += BrewHandler.calculateRating(j.getAmount(), i.getAmount(), recipe.getDifficulty());

					found = j;
					break;
				}
			}

			if(found == null) {
				result --;
			} else {
				is.remove(found);
			}
		}

		return BrewHandler.calculateRating(result, recipe.getIngredients().size() * 2, recipe.getDifficulty());
	}
}
