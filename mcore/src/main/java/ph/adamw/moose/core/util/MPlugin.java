package ph.adamw.moose.core.util;

import org.bukkit.plugin.java.JavaPlugin;
import ph.adamw.moose.core.MCore;
import ph.adamw.moose.core.util.config.ConfigRegistry;

public abstract class MPlugin extends JavaPlugin {
	@Override
	public void onDisable() {
		ConfigRegistry.saveAll(this);
	}
}
