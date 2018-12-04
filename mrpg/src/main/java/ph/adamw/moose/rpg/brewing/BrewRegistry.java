package ph.adamw.moose.rpg.brewing;

import org.bukkit.inventory.ItemStack;
import ph.adamw.moose.rpg.MRpg;

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

		// Needs at least a score of 2 on any brew to not return null, TODO add this to a general mrpg config
		int cache = 1;

		for(BrewRecipe i : recipes.values()) {
			int score = scoreRecipe(i, items);
			if(scoreRecipe(i, items) > cache) {
				scorer = i;
				cache = score;
			}
		}

		return scorer;
	}

	private int scoreRecipe(BrewRecipe recipe, List<ItemStack> items) {
		double result = 0;

		// Adds 1 for a matching item type, removes 1 for a missing item type
		// this punishes people for chucking in everything
		for(ItemStack i : recipe.getIngredients()) {
			for(ItemStack j : items) {
				if(j.getType().equals(i.getType())) {
					// For correct item
					result ++;

					// Scores along with correct quantities
					result += MRpg.getPlugin().getBrewingHandler().calculateRating(j.getAmount(), i.getAmount(), recipe.getDifficulty());
				} else {
					result --;
				}
			}
		}

		return (int) result;
	}

	public double getRecipeAccuracy(BrewRecipe closestRecipe, List<ItemStack> items) {
		if(closestRecipe == BrewRecipe.NULL_RECIPE) {
			// Doesn't matter - just stops divide by 0 error
			return 1d;
		}

		return MRpg.getPlugin().getBrewingHandler().calculateRating(scoreRecipe(closestRecipe, items), closestRecipe.getIngredients().size() * 2, closestRecipe.getDifficulty());
	}
}
