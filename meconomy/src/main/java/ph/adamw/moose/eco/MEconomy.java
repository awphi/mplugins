package ph.adamw.moose.eco;

import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import ph.adamw.moose.eco.command.CommandBalance;
import ph.adamw.moose.eco.command.CommandPay;
import ph.adamw.moose.eco.command.CommandVault;
import ph.adamw.moose.core.util.config.Config;
import ph.adamw.moose.core.util.command.CommandWrapper;

public class MEconomy extends JavaPlugin {
	@Getter
	private static MEconomy plugin;

	@Getter
	private Config ecoConfig;

	@Getter
	private EconomyHandler economyHandler;
	
	@Override
	public void onEnable() {
		plugin = this;

		ConfigurationSerialization.registerClass(EconomyProfile.class);

		ecoConfig = new Config(this, "eco.yml");
		economyHandler = new EconomyHandler();

		getServer().getPluginManager().registerEvents(economyHandler, this);

		CommandWrapper.registerCommand(this, new CommandPay());
		CommandWrapper.registerCommand(this, new CommandBalance());
		CommandWrapper.registerCommand(this, new CommandVault());
	}

	@Override
	public void onDisable(){
		ecoConfig.save();
	}
}
