package ph.adamw.moose.core;

import lombok.Getter;
import ph.adamw.moose.core.basic.CommandHelp;
import ph.adamw.moose.core.basic.CommandRank;
import ph.adamw.moose.core.util.MPlugin;
import ph.adamw.moose.core.util.command.CommandRegistry;
import ph.adamw.moose.core.util.command.CommandWrapper;
import ph.adamw.moose.core.util.config.ConfigRegistry;

public class MCore extends MPlugin {
	@Getter
	private static MCore plugin;

	@Getter
	private final CommandRegistry commandRegistry = new CommandRegistry();

	@Getter
	private final ConfigRegistry configRegistry = new ConfigRegistry();

	@Override
	public void onEnable() {
		plugin = this;

		CommandWrapper.registerCommand(this, new CommandHelp());
		CommandWrapper.registerCommand(this, new CommandRank());
	}
}
