package ph.adamw.moose.core.util;

import org.bukkit.plugin.java.JavaPlugin;
import ph.adamw.moose.core.MCore;

public abstract class MPlugin extends JavaPlugin {
	@Override
	public void onDisable() {
		MCore.getPlugin().getConfigRegistry().saveAll(this);
	}
}
