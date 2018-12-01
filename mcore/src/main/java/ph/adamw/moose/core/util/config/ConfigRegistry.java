package ph.adamw.moose.core.util.config;

import ph.adamw.moose.core.util.MPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigRegistry {
	private final HashMap<MPlugin, List<Config>> map = new HashMap<>();

	public void register(MPlugin plugin, Config config) {
		if(!map.containsKey(plugin)) {
			map.put(plugin, new ArrayList<>());
		}

		map.get(plugin).add(config);
	}

	public void saveAll(MPlugin plugin) {
		if(!map.containsKey(plugin)) {
			return;
		}

		for(Config i : map.get(plugin)) {
			i.save();
		}
	}
}
