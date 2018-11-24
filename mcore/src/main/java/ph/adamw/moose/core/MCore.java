package ph.adamw.moose.core;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import ph.adamw.moose.core.command.CommandHelp;
import ph.adamw.moose.core.util.command.CommandRegistry;
import ph.adamw.moose.core.util.command.CommandWrapper;

public class MCore extends JavaPlugin {
	@Getter
	private static MCore plugin;

	@Getter
	private final CommandRegistry commandRegistry = new CommandRegistry();

	@Override
	public void onEnable() {
		plugin = this;

		CommandWrapper.registerCommand(this, new CommandHelp());
	}

	@Override
	public void onDisable(){

	}
}
