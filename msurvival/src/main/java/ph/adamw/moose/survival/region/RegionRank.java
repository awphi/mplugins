package ph.adamw.moose.survival.region;

import lombok.Getter;

public enum RegionRank {
	OWNER("Owner"),
	ADMIN("Admin"),
	BUILDER("Builder"),
	VISITOR("Visitor");

	static final RegionRank[] VALUES = values();

	@Getter
	private final String name;

	RegionRank(String name) {
		this.name = name;
	}

	public boolean canPerform(RegionRank rank) {
		return ordinal() <= rank.ordinal();
	}

	public RegionRank nextUp() {
		if(ordinal() - 1 == -1) {
			return this;
		}

		return VALUES[ordinal() - 1];
	}

	public RegionRank nextDown() {
		if(ordinal() + 1 >= VALUES.length) {
			return this;
		}

		return VALUES[ordinal() + 1];
	}
}
