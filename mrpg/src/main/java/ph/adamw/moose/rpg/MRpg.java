package ph.adamw.moose.rpg;

import lombok.Getter;
import ph.adamw.moose.core.util.MPlugin;
import ph.adamw.moose.core.util.multiblock.MultiBlockHandler;
import ph.adamw.moose.rpg.brewing.BrewHandler;
import ph.adamw.moose.rpg.brewing.multiblock.MultiBlockBarrel;
import ph.adamw.moose.rpg.brewing.multiblock.MultiBlockVat;
import ph.adamw.moose.rpg.diet.DietHandler;
import ph.adamw.moose.rpg.fish.FishHandler;
import ph.adamw.moose.rpg.state.RpgPlayerRegistry;

public class MRpg extends MPlugin {
	@Getter
	private static MRpg plugin;

	private FishHandler fishHandler;

	private DietHandler dietHandler;

	@Getter
	private BrewHandler brewHandler;

	@Override
	public void onEnable() {
		plugin = this;

		getServer().getPluginManager().registerEvents(new RpgPlayerRegistry(), this);

		MultiBlockHandler.registerMultiBlock(this, MultiBlockBarrel.class, MultiBlockBarrel.PATTERN);
		MultiBlockHandler.registerMultiBlock(this, MultiBlockVat.class, MultiBlockVat.PATTERN);

		fishHandler = new FishHandler();
		brewHandler = new BrewHandler();
		dietHandler = new DietHandler();
	}
}
