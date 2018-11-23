package ph.adamw.moose.rpg.fish.data;


import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum FishQuality {
	DIRTY("Dirty", 5, 2f),
	CLEAN("Clean", 3, 3f),
	SHINY("Shiny", 2, 5f);

	private static List<FishQuality> VALUES;
	private static int SIZE;

	private static final Random random = new Random();
	private static final FishQuality[] QUALITIES = values();

	private final int weight;

	@Getter
	private final String string;

	@Getter
	private final float multiplier;

	static {
		List<FishQuality> vals = new ArrayList<>();

		for(FishQuality i : values()) {
			for(int j = 0; j < i.weight; j ++) {
				vals.add(i);
			}
		}

		VALUES = vals;
		SIZE = VALUES.size();
	}

	public FishQuality get(int i) {
		return QUALITIES[i];
	}

	FishQuality(String string, int weight, float multiplier) {
		this.string = string;
		this.weight = weight;
		this.multiplier = multiplier;
	}

	public static FishQuality random()  {
		return VALUES.get(random.nextInt(SIZE));
	}
}
