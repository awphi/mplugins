package ph.adamw.moose.survival;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import ph.adamw.moose.core.MCore;
import ph.adamw.moose.core.util.command.CommandArgument;
import ph.adamw.moose.core.util.command.CommandWrapper;
import ph.adamw.moose.core.util.config.Config;
import ph.adamw.moose.survival.region.CommandRegion;
import ph.adamw.moose.survival.region.Region;
import ph.adamw.moose.survival.region.RegionHandler;

public class MSurvival extends JavaPlugin {
	@Getter
	private static MSurvival plugin;

	@Getter
	private RegionHandler regionHandler;

	@Getter
	private Config regionConfig;

	@Override
	public void onEnable() {
		plugin = this;

		ConfigurationSerialization.registerClass(Region.class);

		this.regionConfig = new Config(this, "regions.yml");
		this.regionHandler = new RegionHandler();

		CommandWrapper.registerCommand(this, new CommandRegion());
	}

	@Override
	public void onDisable(){
		regionConfig.save();
	}
}

