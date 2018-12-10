package ph.adamw.moose.rpg.fish.data;

import de.tr7zw.itemnbtapi.NBTItem;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum FishSpecies {
	// Tier 0
	ANCHOVY(FishTier.ZERO, Material.COD, 1, "Anchovy", 5f),
	SARDINE(FishTier.ZERO, Material.COD, 1, "Sardine", 5f),
	MACKEREL(FishTier.ZERO, Material.COD, 1, "Mackerel", 5f),
	MINNOW(FishTier.ZERO, Material.COD, 1, "Minnow", 5f),
	FLATFISH(FishTier.ZERO, Material.COD, 1, "Flatfish", 5f),
	HERRING(FishTier.ZERO, Material.COD, 1, "Herring", 5f),
	HALIBUT(FishTier.ZERO, Material.COD, 1, "Halibut", 5f),
	CHUB(FishTier.ZERO, Material.COD, 1, "Chub", 5f),
	HAKE(FishTier.ZERO, Material.COD, 1, "Hake", 5f),

	// Tier 1
	SALMON(FishTier.ONE, Material.SALMON, 1, "Salmon", 14f),
	HADDOCK(FishTier.ONE, Material.SALMON, 1, "Haddock", 14f),
	COD(FishTier.ONE, Material.SALMON, 1, "Cod", 14f),
	TROUT(FishTier.ONE, Material.SALMON, 1, "Trout", 14f),
	CATFISH(FishTier.ONE, Material.SALMON, 1, "Catfish", 14f),
	BARJACK(FishTier.ONE, Material.SALMON, 1, "Bar Jack", 14f),

	KOI(FishTier.TWO, Material.TROPICAL_FISH, 1, "Koi Carp", 50f, FishEffects.KOI),
	FLYING(FishTier.TWO, Material.TROPICAL_FISH, 1, "Flying Fish", 50f, FishEffects.FLYING),
	RAINBOW(FishTier.TWO, Material.TROPICAL_FISH, 1, "Rainbow Fish", 100f, FishEffects.RAINBOW),
	ANGEL(FishTier.TWO, Material.TROPICAL_FISH, 1, "Angel Fish", 50f, FishEffects.ANGEL),
	FUGU(FishTier.TWO, Material.PUFFERFISH, 1, "Fugu Fish", 75f, FishEffects.FUGU);


	private static List<List<FishSpecies>> TIERS;
	private static final FishSpecies[] SPECIES = values();

	static {
		TIERS = new ArrayList<>();
		for(FishSpecies i : values()) {
			if(i.tier.ordinal() >= TIERS.size()) {
				TIERS.add(new ArrayList<>());
			}

			for(int j = 0; j < i.weight; j ++) {
				TIERS.get(i.tier.ordinal()).add(i);
			}
		}
	}

	private static final Random random = new Random();

	private final int weight;

	@Getter
	private final String string;

	@Getter
	private final FishTier tier;

	@Getter
	private final FishEffects effects;
	
	@Getter
	private final Material material;

	@Getter
	private final float multiplier;

	FishSpecies(FishTier tier, Material material, int weight, String string, float multiplier, FishEffects effects) {
		this.tier = tier;
		this.material = material;
		this.weight = weight;
		this.string = string;
		this.multiplier = multiplier;
		this.effects = effects;
	}

	FishSpecies(FishTier tier, Material material, int weight, String string, float multiplier) {
		this(tier, material, weight, string, multiplier, null);
	}

	public static FishSpecies random()  {
		List<FishSpecies> chosen = TIERS.get(FishTier.random().ordinal());
		return chosen.get(random.nextInt(chosen.size()));
	}

	public static FishSpecies get(int i) {
		return SPECIES[i];
	}

	public enum FishEffects {
		KOI(PotionEffectType.NIGHT_VISION, PotionEffectType.FIRE_RESISTANCE, PotionEffectType.DAMAGE_RESISTANCE),
		FLYING(PotionEffectType.SPEED, PotionEffectType.JUMP),
		RAINBOW(PotionEffectType.FAST_DIGGING),
		ANGEL(PotionEffectType.INVISIBILITY, PotionEffectType.REGENERATION, PotionEffectType.ABSORPTION),
		FUGU(PotionEffectType.CONFUSION, PotionEffectType.SLOW_DIGGING, PotionEffectType.HUNGER, PotionEffectType.SLOW, PotionEffectType.LUCK);

		@Getter
		private final PotionEffectType[] effects;

		public static void apply(ItemStack fish, Player player) {
			final NBTItem wrapper = new NBTItem(fish);
			if(wrapper.hasKey("fishScore")) {
				return;
			}

			final int length = (int) (wrapper.getFloat("fishWeight") * 100f);
			final int potency = wrapper.getInteger("fishQuality") + 2;

			final FishEffects effects = FishSpecies.get(wrapper.getInteger("fishSpecies")).getEffects();

			for(PotionEffectType type : effects.getEffects()) {
				player.addPotionEffect(new PotionEffect(type, length, potency), true);
			}
		}

		FishEffects(PotionEffectType... effects) {
			this.effects = effects;
		}
	}
}
