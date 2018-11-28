package ph.adamw.moose.core;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import ph.adamw.moose.core.basic.CommandHelp;
import ph.adamw.moose.core.basic.CommandRank;
import ph.adamw.moose.core.perms.RankHandler;
import ph.adamw.moose.core.util.config.Config;
import ph.adamw.moose.core.util.command.CommandRegistry;
import ph.adamw.moose.core.util.command.CommandWrapper;

public class MCore extends JavaPlugin {
	@Getter
	private static MCore plugin;

	@Getter
	private final CommandRegistry commandRegistry = new CommandRegistry();

	@Getter
	private RankHandler rankHandler;

	@Getter
	private Config rankConfig;

	@Getter
	private Config syncConfig;

	@Override
	public void onEnable() {
		plugin = this;

		this.syncConfig = new Config(this, "sync.yml", "sync.yml");

		this.rankHandler = new RankHandler();

		CommandWrapper.registerCommand(this, new CommandHelp());
		CommandWrapper.registerCommand(this, new CommandRank());
	}

	@Override
	public void onDisable(){
		rankConfig.save();
	}
}
