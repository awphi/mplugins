package ph.adamw.moose.rpg.fish.data;

import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum FishTier {
	ZERO(14, ChatColor.GREEN),
	ONE(5, ChatColor.LIGHT_PURPLE),
	TWO(1, ChatColor.GOLD);

	private static List<FishTier> VALUES;
	private static int SIZE;
	private static final Random random = new Random();

	private final int weight;

	@Getter
	private final ChatColor color;

	static {
		List<FishTier> vals = new ArrayList<>();

		for(FishTier i : values()) {
			for(int j = 0; j < i.weight; j ++) {
				vals.add(i);
			}
		}

		VALUES = vals;
		SIZE = VALUES.size();
	}

	FishTier(int weight, ChatColor color) {
		this.weight = weight;
		this.color = color;
	}

	public static FishTier random()  {
		return VALUES.get(random.nextInt(SIZE));
	}
}
