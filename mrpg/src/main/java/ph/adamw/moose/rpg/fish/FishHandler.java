package ph.adamw.moose.rpg.fish;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import ph.adamw.moose.rpg.fish.data.FishQuality;
import ph.adamw.moose.rpg.fish.data.FishSpecies;
import ph.adamw.moose.util.nbt.ItemStackWrapper;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class FishHandler implements Listener {
	private static Random random = new Random();
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private static DecimalFormat decimalFormat = new DecimalFormat();

	private static final Enchantment FISHERMANS_DELIGHT = new FishermansDelightWrapper();

	static {
		decimalFormat.setMaximumFractionDigits(2);
	}

	private static ItemStack getStack(FishSpecies species, float weight, FishQuality quality, Player catcher) {
		ItemStackWrapper wrapper = new ItemStackWrapper(species.getMaterial(), 1);

		// Name
		final ChatColor col = species.getTier().getColor();
		wrapper.setName(col + "" + ChatColor.ITALIC + "" + quality.getString() + ChatColor.RESET + " " + col + "" + species.getString() + ChatColor.RESET + " - " + col + decimalFormat.format(weight) + "lb");

		// Lore
		wrapper.setLore("Caught by: " + catcher.getDisplayName() + " on " + dateFormat.format(new Date()));

		// NBT
		wrapper.setInt("fishSpecies", species.ordinal());
		wrapper.setFloat("fishWeight", weight);
		wrapper.setInt("fishQuality", quality.ordinal());
		wrapper.setInt("fishScore", (int) (weight * quality.getMultiplier() * species.getMultiplier()));

		return wrapper.getItemStack();
	}

	private static ItemStack getRandomFish(Player catcher) {
		return getStack(FishSpecies.random(), random.nextFloat() * (20f - 0.1f) + 0.1f, FishQuality.random(), catcher);
	}

	@EventHandler
	public void onFish(PlayerFishEvent event) {
		final ItemStack heldItem = event.getPlayer().getInventory().getItemInMainHand();
		if(heldItem.getType() != Material.FISHING_ROD || !heldItem.containsEnchantment(FISHERMANS_DELIGHT)) {
			return;
		}

		if(event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
			event.getPlayer().getInventory().addItem(getRandomFish(event.getPlayer()));
		}
	}

	@EventHandler
	public void onEat(PlayerItemConsumeEvent event) {
		ItemStackWrapper wrapper = new ItemStackWrapper(event.getItem());
		if(event.getItem().getType() == Material.COD && wrapper.isTagEmpty("fishScore")) {
			FishSpecies.FishEffects.apply(event.getItem(), event.getPlayer());
		}
	}
}
