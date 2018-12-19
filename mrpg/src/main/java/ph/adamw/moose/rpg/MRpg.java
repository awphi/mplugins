package ph.adamw.moose.rpg;

import lombok.Getter;
import ph.adamw.moose.core.util.MPlugin;
import ph.adamw.moose.core.util.multiblock.MultiBlockHandler;
import ph.adamw.moose.rpg.brewing.BrewHandler;
import ph.adamw.moose.rpg.brewing.multiblock.MultiBlockBarrel;
import ph.adamw.moose.rpg.brewing.multiblock.MultiBlockVat;
import ph.adamw.moose.rpg.fish.FishHandler;

public class MRpg extends MPlugin {
	@Getter
	private static MRpg plugin;

	private FishHandler fishHandler;

	@Getter
	private BrewHandler brewHandler;

	@Override
	public void onEnable() {
		plugin = this;

		MultiBlockHandler.registerMultiBlock(this, MultiBlockBarrel.class, MultiBlockBarrel.PATTERN);
		MultiBlockHandler.registerMultiBlock(this, MultiBlockVat.class, MultiBlockVat.PATTERN);

		fishHandler = new FishHandler();
		brewHandler = new BrewHandler();
	}
}
