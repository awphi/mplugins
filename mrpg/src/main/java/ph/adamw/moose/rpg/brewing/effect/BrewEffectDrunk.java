package ph.adamw.moose.rpg.brewing.effect;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import ph.adamw.moose.rpg.MRpg;

import java.util.concurrent.ThreadLocalRandom;

public class BrewEffectDrunk extends BrewEffect implements Listener {
	private final static transient String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

	public BrewEffectDrunk() {
		super("DRUNK");
		MRpg.getPlugin().getServer().getPluginManager().registerEvents(this, MRpg.getPlugin());
	}

	@Override
	public void run(Player player, int potency, int length) {
		BrewEffect.addCustomEffect(player, this, potency, length);
		applyNausea(player, this);
	}

	@EventHandler
	public void onMessage(AsyncPlayerChatEvent event) {
		if(BrewEffect.hasEffect(event.getPlayer(), this, 1)) {
			event.setMessage(drunkifyString(event.getMessage(), BrewEffect.getPotencyOfEffect(event.getPlayer(), this)));
		}
	}

	private static String drunkifyString(String string, int potency) {
		final StringBuilder result = new StringBuilder();
		final int drunkRating = 8 - potency;

		for(String word : string.split(" ")) {
			word = word.toLowerCase();

			final int n = word.split("").length;

			final double addChance = Math.floor(ThreadLocalRandom.current().nextDouble() * 10d);

			// Adds a letter in randomly to 50% of words at potency 3
			if(addChance >= drunkRating) {
				final String letter = ALPHABET.split("")[ThreadLocalRandom.current().nextInt(26)];
				final int pos = ThreadLocalRandom.current().nextInt(n);
				word = word.substring(0, pos) + letter + word.substring(pos, word.length());
			}

			for(int i = 0; i < word.split("").length; i ++) {
				// 0 to 10
				final double mixChance = Math.floor(ThreadLocalRandom.current().nextDouble() * 10d);
				if (mixChance >= drunkRating + 3) {
					final int pos = ThreadLocalRandom.current().nextInt(word.split("").length);
					final char[] chars = word.toCharArray();
					final char cache = chars[pos];
					chars[pos] = chars[i];
					chars[i] = cache;

					word = String.valueOf(chars);
				}
			}

			result.append(word);
			result.append(" ");
		}

		return result.toString().trim();
	}

	private void applyNausea(Player player, BrewEffectDrunk effect) {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!BrewEffect.hasEffect(player, effect, 1)) {
					cancel();
					return;
				}

				player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION,200 + ThreadLocalRandom.current().nextInt(200), 1), true);
				applyNausea(player, effect);
			}
		}.runTaskLater(MRpg.getPlugin(), ThreadLocalRandom.current().nextLong(200));
	}
}
