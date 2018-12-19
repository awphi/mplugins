package ph.adamw.moose.rpg.brewing.effect;

import de.tr7zw.itemnbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffectType;
import ph.adamw.moose.rpg.MRpg;
import ph.adamw.moose.rpg.brewing.BrewHandler;
import ph.adamw.moose.rpg.brewing.BrewRegistry;

import java.util.List;
import java.util.logging.Level;

public class BrewEffectListener implements Listener {
	@EventHandler
	public void onDrink(PlayerItemConsumeEvent event) {
		if(!event.getItem().getType().equals(Material.POTION)) {
			return;
		}

		final NBTItem nbt = new NBTItem(event.getItem());

		if(!nbt.hasKey(BrewHandler.CLOSEST_RECIPE)) {
			return;
		}

		final List<String> effects = BrewRegistry.getBrew(nbt.getString(BrewHandler.CLOSEST_RECIPE)).getEffects();

		for(String i : effects) {
			if(!BrewEffect.isValidEffectString(i)) {
				MRpg.getPlugin().getLogger().log(Level.SEVERE, "Could not apply unresolved potion effect: " + i + "!");
				continue;
			}

			final String[] split = i.split(";");
			final String[] namePotency = split[0].split("\\s+");

			final double ageRating = nbt.getDouble(BrewHandler.AGE_RATING);
			final double brewAccuracy;

			if(ageRating != -1d) {
				brewAccuracy = (nbt.getDouble(BrewHandler.COOK_RATING) + nbt.getDouble(BrewHandler.INGREDIENTS_RATING)) / 2d;
			} else {
				brewAccuracy = (ageRating + nbt.getDouble(BrewHandler.COOK_RATING) + nbt.getDouble(BrewHandler.INGREDIENTS_RATING)) / 3d;
			}


			final String name = namePotency[0].toUpperCase();
			final int potency = (int) Math.round(Integer.valueOf(namePotency[1]) * brewAccuracy);
			final int length = (int) (Double.parseDouble(split[1]) * 60d * 20d * brewAccuracy);

			final BrewEffect effect = BrewRegistry.getCustomEffect(name);

			if(effect == null) {
				event.getPlayer().addPotionEffect(PotionEffectType.getByName(name).createEffect(length, potency - 1), true);
			} else {
				effect.run(event.getPlayer(), potency, length);
			}
		}
	}
}
