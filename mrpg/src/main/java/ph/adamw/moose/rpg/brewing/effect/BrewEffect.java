package ph.adamw.moose.rpg.brewing.effect;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import ph.adamw.moose.rpg.MRpg;
import ph.adamw.moose.rpg.brewing.BrewRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public abstract class BrewEffect {
	@Getter
	private final String name;

	private static transient final Map<UUID, List<BrewCustomEffect>> effectMap = new HashMap<>();

	public BrewEffect(String name) {
		this.name = name;
	}

	public void run(Player player, int potency, int length) {
		BrewEffect.addCustomEffect(player, this, potency, length);
		applyExtraEffects(player, potency, length);
	}

	public abstract void applyExtraEffects(Player player, int potency, int length);

	public static boolean isValidEffectString(String effect) {
		final String[] split = effect.split(";");
		final String[] namePotency = split[0].split("\\s+");

		final boolean isEffect = BrewRegistry.getCustomEffect(namePotency[0]) != null || PotionEffectType.getByName(namePotency[0]) != null;

		return isEffect && namePotency[1].matches("[1-9][0-9]*") && split[1].matches("^[0-9]\\d*(\\.\\d+)?$");
	}

	public static void addCustomEffect(Player player, BrewEffect effect, int potency, int length) {
		final BrewCustomEffect inst = new BrewCustomEffect(effect, potency);

		if(effectMap.containsKey(player.getUniqueId())) {
			effectMap.get(player.getUniqueId()).add(inst);
		} else {
			effectMap.put(player.getUniqueId(), new ArrayList<>(Collections.singleton(inst)));
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				effectMap.get(player.getUniqueId()).remove(inst);
			}
		}.runTaskLater(MRpg.getPlugin(), length);
	}

	public static int getPotencyOfEffect(Player player, BrewEffect effect) {
		if(player.isOnline()) {
			for(BrewCustomEffect customEffect : getEffects(player)) {
				if(customEffect.getEffect().equals(effect)) {
					return customEffect.getPotency();
				}
			}
		}

		return -1;
	}

	public static boolean hasEffect(Player player, BrewEffect effect, int potencyAtLeast) {
		final int p = getPotencyOfEffect(player, effect);
		return p != -1 && p >= potencyAtLeast;
	}

	public static List<BrewCustomEffect> getEffects(Player player) {
		if(effectMap.containsKey(player.getUniqueId())) {
			return effectMap.get(player.getUniqueId());
		}

		return new ArrayList<>();
	}

	@AllArgsConstructor
	@Getter
	public static class BrewCustomEffect {
		private final BrewEffect effect;
		private final int potency;
	}

	protected void applyTemporaryPulsingEffect(Player player, int potencyNeeded, PotionEffectType type, int amplifier, int duration) {
		final BrewEffect effect = this;
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!BrewEffect.hasEffect(player, effect, potencyNeeded)) {
					cancel();
					return;
				}

				player.addPotionEffect(new PotionEffect(type,duration + ThreadLocalRandom.current().nextInt(duration / 2), amplifier), true);
				applyTemporaryPulsingEffect(player, potencyNeeded, type, amplifier, duration);
			}
		}.runTaskLater(MRpg.getPlugin(), ThreadLocalRandom.current().nextLong(duration));
	}
}
