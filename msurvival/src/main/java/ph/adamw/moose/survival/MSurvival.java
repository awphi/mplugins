package ph.adamw.moose.survival;

import lombok.Getter;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import ph.adamw.moose.core.util.MPlugin;
import ph.adamw.moose.core.util.command.CommandWrapper;
import ph.adamw.moose.survival.region.CommandRegion;
import ph.adamw.moose.survival.region.Region;
import ph.adamw.moose.survival.region.RegionHandler;

public class MSurvival extends MPlugin {
	@Getter
	private static MSurvival plugin;

	@Getter
	private RegionHandler regionHandler;

	@Override
	public void onEnable() {
		plugin = this;

		ConfigurationSerialization.registerClass(Region.class);

		this.regionHandler = new RegionHandler();

		CommandWrapper.registerCommand(this, new CommandRegion());
	}
}

