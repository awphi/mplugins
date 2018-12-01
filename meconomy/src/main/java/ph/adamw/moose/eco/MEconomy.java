package ph.adamw.moose.eco;

import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import ph.adamw.moose.core.util.MPlugin;
import ph.adamw.moose.eco.command.CommandBalance;
import ph.adamw.moose.eco.command.CommandPay;
import ph.adamw.moose.eco.command.CommandVault;
import ph.adamw.moose.core.util.command.CommandWrapper;

public class MEconomy extends MPlugin {
	@Getter
	private static MEconomy plugin;

	@Getter
	private EconomyHandler economyHandler;
	
	@Override
	public void onEnable() {
		plugin = this;

		ConfigurationSerialization.registerClass(EconomyProfile.class);

		economyHandler = new EconomyHandler();

		getServer().getPluginManager().registerEvents(economyHandler, this);

		CommandWrapper.registerCommand(this, new CommandPay());
		CommandWrapper.registerCommand(this, new CommandBalance());
		CommandWrapper.registerCommand(this, new CommandVault());
	}
}
