package ph.adamw.moose.rpg.diet;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public enum FoodGroup {
	FRUIT_AND_VEG(Material.APPLE, Material.CHORUS_FRUIT, Material.GOLDEN_APPLE, Material.ENCHANTED_GOLDEN_APPLE, Material.MELON_SLICE, Material.BEETROOT_SOUP, Material.POISONOUS_POTATO, Material.BEETROOT, Material.CARROT, Material.POTATO, Material.BAKED_POTATO, Material.GOLDEN_CARROT),
	PROTEIN(Material.MUSHROOM_STEW, Material.RABBIT_STEW, Material.SPIDER_EYE, Material.BEEF, Material.COOKED_BEEF, Material.ROTTEN_FLESH, Material.MUTTON, Material.COOKED_MUTTON, Material.PORKCHOP, Material.COOKED_PORKCHOP, Material.RABBIT, Material.COOKED_RABBIT, Material.CHICKEN, Material.COOKED_CHICKEN, Material.COD, Material.SALMON, Material.TROPICAL_FISH, Material.PUFFERFISH),
	CARBOHYDRATE(Material.PUMPKIN_PIE, Material.BREAD, Material.CAKE, Material.COOKIE, Material.KELP),
	LIQUID;

	final List<Material> types;

	private static final FoodGroup[] VALUES = values();

	FoodGroup(Material... types) {
		this.types = Arrays.asList(types);
	}

	public static FoodGroup getFoodGroup(Material type) {
		for(FoodGroup i : VALUES) {
			if(i.types.contains(type)) {
				return i;
			}
		}

		return CARBOHYDRATE;
	}
}
