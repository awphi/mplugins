package ph.adamw.moose.rpg;

import lombok.Getter;
import org.bukkit.Bukkit;
import ph.adamw.moose.core.MCore;
import ph.adamw.moose.core.util.MPlugin;
import ph.adamw.moose.eco.MEconomy;
import ph.adamw.moose.rpg.brewing.BrewingHandler;
import ph.adamw.moose.rpg.brewing.MultiBlockBarrel;
import ph.adamw.moose.rpg.brewing.MultiBlockVat;
import ph.adamw.moose.rpg.fish.FishHandler;
import org.bukkit.plugin.java.JavaPlugin;

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

		MCore.getPlugin().getMultiBlockHandler().registerMultiBlock(new MultiBlockBarrel());
		MCore.getPlugin().getMultiBlockHandler().registerMultiBlock(new MultiBlockVat());

		fishHandler = new FishHandler();
		brewingHandler = new BrewingHandler();
	}
}
