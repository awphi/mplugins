package ph.adamw.moose.rpg.state;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ph.adamw.moose.core.util.config.AutoSerializable;
import ph.adamw.moose.rpg.brewing.BrewHandler;
import ph.adamw.moose.rpg.diet.FoodGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RpgPlayer extends AutoSerializable {
	final String uuid;
	final List<String> diet = new ArrayList<>();

	public RpgPlayer(OfflinePlayer player) {
		this.uuid = player.getUniqueId().toString();
	}

	public static RpgPlayer from(OfflinePlayer player) {
		return RpgPlayerRegistry.getRpgPlayer(player);
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(UUID.fromString(uuid));
	}

	// 1.0 = perfect, lower = less perfect with a lower limit of 0.0
	public float getDietAccuracy() {
		final Map<String, Integer> counts = new HashMap<>();

		for(String i : diet) {
			final Integer c = counts.get(i);
			counts.put(i, c != null ? c + 1 : 1);
		}

		float result = 0;

		for(String i : counts.keySet()) {
			// Follows normal distribution
			result += BrewHandler.calculateRating((float) counts.get(i) / (float) diet.size(),1f / (float) FoodGroup.values().length, 1);
		}

		return result / counts.keySet().size();
	}

	public void consume(FoodGroup group, int foodLevel, float saturation) {
		final Player player = getPlayer();
		if(player.isOnline()) {
			diet.add(0, group.name());
			if(diet.size() > FoodGroup.values().length * 5) {
				diet.remove(diet.size() - 1);
			}

			final float balance = getDietAccuracy();
			player.setSaturation(player.getSaturation() + (saturation * balance));
			player.setFoodLevel(player.getFoodLevel() + (int) (foodLevel * balance));
		}
	}
}
