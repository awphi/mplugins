package ph.adamw.moose.rpg.brewing;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrewRegistry {
	private final Map<String, BrewRecipe> recipes = new HashMap<>();

	public void register(BrewRecipe recipe) {
		recipes.put(recipe.getName(), recipe);
	}

	public BrewRecipe get(String name) {
		return recipes.get(name);
	}

	public BrewRecipe getClosestRecipe(List<ItemStack> items) {
		BrewRecipe scorer = BrewRecipe.NULL_RECIPE;

		// Needs at least a rating of at least 0.2 on any brew to not return null, TODO add this to a general mrpg config
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
					result += BrewingHandler.calculateRating(j.getAmount(), i.getAmount(), recipe.getDifficulty());

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

		return BrewingHandler.calculateRating(result, recipe.getIngredients().size() * 2, recipe.getDifficulty());
	}

	public static double getRecipeAccuracy(BrewRecipe closestRecipe, List<ItemStack> items) {
		if(closestRecipe == BrewRecipe.NULL_RECIPE) {
			// Doesn't matter - just stops divide by 0 error
			return 1d;
		}

		return rateRecipeAttempt(closestRecipe, items);
	}
}
