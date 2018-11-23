package ph.adamw.moose.eco;

import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import ph.adamw.moose.eco.command.CommandBalance;
import ph.adamw.moose.eco.command.CommandPay;
import ph.adamw.moose.util.PluginFile;
import ph.adamw.moose.util.command.CommandWrapper;;

public class MEconomy extends JavaPlugin {
	@Getter
	private static MEconomy plugin;

	@Getter
	private PluginFile economyStore;

	@Getter
	private EconomyHandler economyHandler;
	
	@Override
	public void onEnable() {
		plugin = this;
		ConfigurationSerialization.registerClass(EconomyProfile.class);

		economyStore = new PluginFile(this, "eco.yml");
		economyHandler = new EconomyHandler();

		getServer().getPluginManager().registerEvents(economyHandler, this);

		CommandWrapper.registerCommand(this, new CommandPay());
		CommandWrapper.registerCommand(this, new CommandBalance());
	}

	@Override
	public void onDisable(){
		economyStore.save();
	}
}
