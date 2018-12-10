package ph.adamw.moose.core.util.command;

import org.bukkit.Bukkit;
import ph.adamw.moose.core.MCore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandRegistry {
	private static Map<String, CommandWrapper> map = new HashMap<>();

	public static void register(CommandWrapper wrapper) {
		map.put(wrapper.getBase().toLowerCase(), wrapper);
		final List<String> aliases = Bukkit.getPluginCommand(wrapper.getBase()).getAliases();

		if(aliases.size() <= 0) {
			return;
		}

		for(String i : aliases) {
			map.put(i, wrapper);
		}
	}

	public static void deregister(String str) {
		map.remove(str.toLowerCase());
	}

	public static boolean isRegistered(String arg) {
		return map.containsKey(arg.toLowerCase());
	}

	public static CommandWrapper getWrapper(String arg) {
		return map.get(arg.toLowerCase());
	}
}
