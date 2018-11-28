package ph.adamw.moose.rpg;

import lombok.Getter;
import org.bukkit.Bukkit;
import ph.adamw.moose.eco.MEconomy;
import ph.adamw.moose.rpg.fish.FishHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class MRpg extends JavaPlugin {
	public static MEconomy ECONOMY;

	@Getter
	private static MRpg plugin;

	private FishHandler fishHandler;

	@Override
	public void onEnable() {
		plugin = this;
		ECONOMY = (MEconomy) Bukkit.getPluginManager().getPlugin("mEconomy");

		fishHandler = new FishHandler();
	}

	@Override
	public void onDisable(){
	}
}
