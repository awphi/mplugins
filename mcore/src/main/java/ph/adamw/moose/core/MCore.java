package ph.adamw.moose.core;

import lombok.Getter;
import ph.adamw.moose.core.basic.CommandHelp;
import ph.adamw.moose.core.basic.CommandRank;
import ph.adamw.moose.core.perms.RankListener;
import ph.adamw.moose.core.util.MPlugin;
import ph.adamw.moose.core.util.command.CommandWrapper;

public class MCore extends MPlugin {
	@Getter
	private static MCore plugin;

	@Override
	public void onEnable() {
		plugin = this;

		MCore.getPlugin().getServer().getPluginManager().registerEvents(new RankListener(), MCore.getPlugin());
		//MCore.getPlugin().getServer().getPluginManager().registerEvents(new EnchantListener(), MCore.getPlugin());

		CommandWrapper.registerCommand(this, new CommandHelp());
		CommandWrapper.registerCommand(this, new CommandRank());
	}
}
