package ph.adamw.moose.core.util.multiblock.pattern;

import org.bukkit.Location;
import org.bukkit.block.Block;
import ph.adamw.moose.core.MCore;
import ph.adamw.moose.core.util.multiblock.MultiBlockHandler;

import java.util.ArrayList;
import java.util.List;

public class MultiBlockPattern {
	private final MultiBlockElement[] elements;

	public MultiBlockPattern(MultiBlockElement core, MultiBlockElement... elements) {
		this.elements = new MultiBlockElement[elements.length + 1];

		this.elements[0] = core;
		System.arraycopy(elements, 0, this.elements, 1, elements.length + 1 - 1);
	}

	public boolean isValidNewStructure(Location origin) {
		final List<Location> x = traversePattern(origin, true);
		return x != null && x.size() == elements.length;
	}

	private List<Location> traversePattern(Location origin, boolean creation) {
		for(int i = 0; i < 4; i ++) {
			final List<Location> x = traverseDirection(origin, i, creation);

			if(x != null) {
				return x;
			}
		}

		return null;
	}

	public List<Location> traversePattern(Location origin) {
		return traversePattern(origin, false);
	}

	private List<Location> traverseDirection(Location origin, int rotationsFromEast, boolean creation) {
		final List<Location> result = new ArrayList<>();

		final int x = origin.getBlockX();
		final int y = origin.getBlockY();
		final int z = origin.getBlockZ();

		for(MultiBlockElement element : elements) {
			int realXDisplacement = (rotationsFromEast % 2 != 0 ? element.getZ() : element.getX());
			int realZDisplacement = (rotationsFromEast % 2 != 0 ? element.getX() : element.getZ());

			switch (rotationsFromEast) {
				case 1: {
					realXDisplacement *= -1;
				} break;
				case 2: {
					realXDisplacement *= -1;
					realZDisplacement *= -1;
				} break;
				case 3: {
					realZDisplacement *= -1;
				} break;
			}

			final Block block = origin.getWorld().getBlockAt(x + realXDisplacement, y + element.getY(), z + realZDisplacement);

			if(element.equals(block, rotationsFromEast)) {
				if(creation && MultiBlockHandler.getProtectionSection().contains(block.getLocation().toString())) {
					return null;
				}

				result.add(block.getLocation());
			} else {
				return null;
			}
		}

		return result;
	}
}
