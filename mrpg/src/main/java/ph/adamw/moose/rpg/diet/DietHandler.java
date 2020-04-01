package ph.adamw.moose.rpg.diet;

import com.google.common.collect.ImmutableMap;
import org.bukkit.Material;
import ph.adamw.moose.core.util.config.Config;
import ph.adamw.moose.rpg.MRpg;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class DietHandler {
	private static final Config foodConfig = new Config(MRpg.getPlugin(), "food.yml", "food.yml", false);
	private static final Map<Material, FoodData> FOOD_MAP;

	static {
		// Parse the food config
		final Map<Material, FoodData> map = new HashMap<>();

		for(String i : foodConfig.getRoot().getKeys(false)) {
			final Material m = Material.getMaterial(i.toUpperCase());
			if(m != null) {
				map.put(m, new FoodData(foodConfig.getConfigurationSection(i).getInt("value"), (float) foodConfig.getConfigurationSection(i).getDouble("saturation")));
			} else {
				MRpg.getPlugin().getLogger().log(Level.SEVERE, "Unrecognised material in food yml: " + i.toUpperCase() + ". Skipping...");
			}
		}

		FOOD_MAP = new ImmutableMap.Builder<Material, FoodData>().putAll(map).build();
	}

	public DietHandler() {
		MRpg.getPlugin().getServer().getPluginManager().registerEvents(new DietListener(), MRpg.getPlugin());
	}

	public static FoodData getFoodData(Material type) {
		return FOOD_MAP.get(type);
	}
}
