package ph.adamw.moose.rpg.fish;

import de.tr7zw.itemnbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ph.adamw.moose.rpg.MRpg;
import ph.adamw.moose.rpg.fish.data.FishQuality;
import ph.adamw.moose.rpg.fish.data.FishSpecies;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

public class FishHandler implements Listener {
	private static Random random = new Random();
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private static DecimalFormat decimalFormat = new DecimalFormat();

	static {
		decimalFormat.setMaximumFractionDigits(2);
	}

	public FishHandler() {
		MRpg.getPlugin().getServer().getPluginManager().registerEvents(this, MRpg.getPlugin());
	}

	private static ItemStack getStack(FishSpecies species, float weight, FishQuality quality, Player catcher) {
		final ItemStack fish = new ItemStack(species.getMaterial(), 1);
		final ItemMeta meta = fish.getItemMeta();

		final ChatColor col = species.getTier().getColor();

		meta.setDisplayName(col + "" + ChatColor.ITALIC + "" + quality.getString() + ChatColor.RESET + " " + col + "" + species.getString() + ChatColor.RESET + " - " + col + decimalFormat.format(weight) + "lb");
		meta.setLore(Collections.singletonList("Caught by: " + catcher.getDisplayName() + " on " + dateFormat.format(new Date())));

		fish.setItemMeta(meta);

		final NBTItem wrapper = new NBTItem(fish);

		// NBT
		wrapper.setInteger("fishSpecies", species.ordinal());
		wrapper.setFloat("fishWeight", weight);
		wrapper.setInteger("fishQuality", quality.ordinal());
		wrapper.setInteger("fishScore", (int) (weight * quality.getMultiplier() * species.getMultiplier()));

		return wrapper.getItem();
	}

	private static ItemStack getRandomFish(Player catcher) {
		return getStack(FishSpecies.random(), random.nextFloat() * (20f - 0.1f) + 0.1f, FishQuality.random(), catcher);
	}

	@EventHandler
	public void onFish(PlayerFishEvent event) {
		if(event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
			event.getPlayer().getInventory().addItem(getRandomFish(event.getPlayer()));
		}
	}

	@EventHandler
	public void onEat(PlayerItemConsumeEvent event) {
		final NBTItem wrapper = new NBTItem(event.getItem());
		if(event.getItem().getType() == Material.COD && wrapper.hasKey("fishScore")) {
			FishSpecies.FishEffects.apply(event.getItem(), event.getPlayer());
		}
	}
}
