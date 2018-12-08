package ph.adamw.moose.rpg;

import lombok.Getter;
import org.bukkit.Bukkit;
import ph.adamw.moose.core.MCore;
import ph.adamw.moose.core.util.MPlugin;
import ph.adamw.moose.core.util.multiblock.MultiBlockHandler;
import ph.adamw.moose.eco.MEconomy;
import ph.adamw.moose.rpg.brewing.BrewingHandler;
import ph.adamw.moose.rpg.brewing.multiblock.MultiBlockBarrel;
import ph.adamw.moose.rpg.brewing.multiblock.MultiBlockVat;
import ph.adamw.moose.rpg.fish.FishHandler;

public class MRpg extends MPlugin {
	@Getter
	private static MEconomy economy;

	@Getter
	private static MRpg plugin;

	private FishHandler fishHandler;

	@Getter
	private BrewingHandler brewingHandler;

	@Override
	public void onEnable() {
		plugin = this;

		economy = (MEconomy) Bukkit.getPluginManager().getPlugin("mEconomy");

		MultiBlockHandler.registerMultiBlock(new MultiBlockBarrel());
		MultiBlockHandler.registerMultiBlock(new MultiBlockVat());

		fishHandler = new FishHandler();
		brewingHandler = new BrewingHandler();
	}
}
