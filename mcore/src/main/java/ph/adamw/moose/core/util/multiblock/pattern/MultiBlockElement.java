package ph.adamw.moose.core.util.multiblock.pattern;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class MultiBlockElement {
	@Getter
	private final Material material;

	@Getter
	private final int x;

	@Getter
	private final int y;

	@Getter
	private final int z;


	public MultiBlockElement(Material material, int x, int y, int z) {
		this.material = material;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public boolean equals(Block block, int rotations) {
		return block.getType().equals(material);
	}
}
