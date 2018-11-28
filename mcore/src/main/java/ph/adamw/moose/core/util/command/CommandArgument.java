package ph.adamw.moose.core.util.command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ph.adamw.moose.core.MCore;

import java.util.HashMap;

public abstract class CommandArgument<T> {
	private final static HashMap<String, CommandArgument> registry = new HashMap<>();

	static {
		register(new CommandArgument<Player>("[player]") {
			@Override
			public String getInvalidDataString(String arg) {
				return "{" + arg + "} isn't currently online.";
			}

			@Override
			public boolean isSyntaxValid(String arg) {
				return !arg.isEmpty();
			}

			@Override
			public Player getObjectFromArg(String arg, CommandSender sender) {
				for(Player i : Bukkit.getOnlinePlayers()) {
					if(i.getName().equals(arg)) {
						return i;
					}
				}

				return null;
			}

			@Override
			public String toHumanString() {
				return "online player";
			}
		});

		register(new CommandArgument<OfflinePlayer>("[offlineplayer]") {
			@Override
			public String getInvalidDataString(String arg) {
				return "{" + arg + "} has never logged on.";
			}

			@Override
			public boolean isSyntaxValid(String arg) {
				return !arg.isEmpty();
			}

			@Override
			public OfflinePlayer getObjectFromArg(String arg, CommandSender sender) {
				for(OfflinePlayer i : Bukkit.getOfflinePlayers()) {
					if(i.getName().equalsIgnoreCase(arg)) {
						return i;
					}
				}

				return null;
			}

			@Override
			public String toHumanString() {
				return "player";
			}
		});

		register(new CommandArgument<Integer>("[integer]") {
			@Override
			public String getInvalidDataString(String arg) {
				return "{" + arg + "} is not an integer.";
			}

			@Override
			public boolean isSyntaxValid(String arg) {
				return arg.matches("[1-9][0-9]*");
			}

			@Override
			public Integer getObjectFromArg(String arg, CommandSender sender) {
				return Integer.valueOf(arg);
			}

			@Override
			public String toHumanString() {
				return "integer";
			}
		});

		register(new CommandArgument<Double>("[double]") {
			@Override
			public String getInvalidDataString(String arg) {
				return "{" + arg + "} is not a rational number.";
			}

			@Override
			public boolean isSyntaxValid(String arg) {
				return arg.matches("[1-9][0-9]*(.[0-9]+)?");
			}

			@Override
			public Double getObjectFromArg(String arg, CommandSender sender) {
				return Double.valueOf(arg);
			}

			@Override
			public String toHumanString() {
				return "decimal";
			}
		});

		register(new CommandArgument<CommandWrapper>("[mcommand]") {
			@Override
			public String getInvalidDataString(String arg) {
				return "{" + arg + "} is not a recognised basic on this server.";
			}

			@Override
			public boolean isSyntaxValid(String arg) {
				return !arg.isEmpty();
			}

			@Override
			public CommandWrapper getObjectFromArg(String arg, CommandSender sender) {
				return MCore.getPlugin().getCommandRegistry().getWrapper(arg);
			}

			@Override
			public String toHumanString() {
				return "basic";
			}
		});

		register(new CommandArgument<String>("[string]") {
			@Override
			public String getInvalidDataString(String arg) {
				// Should never call unless [string] has been used in the wrong context
				// for fixed string syntaces, use them literally when defining syntax (w/o square brackets) e.g.
				// /admin economy add [offlineplayer] [integer]
				// /admin economy remove [offlineplayer] integer

				return "Didn't recognise {" + arg + "} as a string";
			}

			@Override
			public boolean isSyntaxValid(String arg) {
				return !arg.isEmpty();
			}

			@Override
			public String getObjectFromArg(String arg, CommandSender sender) {
				return arg;
			}

			@Override
			public String toHumanString() {
				return "text";
			}
		});
	}

	public static void register(CommandArgument<?> e) {
		registry.put(e.pattern, e);
	}

	public static CommandArgument fromPattern(String pattern) {
		if(!registry.containsKey(pattern)) {
			throw new RuntimeException("Unknown basic argument: " + pattern + ", is this a typo or did you not registerConfig it?");
		}

		return registry.get(pattern);
	}

	private final String pattern;

	protected CommandArgument(String pattern) {
		this.pattern = pattern;
	}

	public abstract String getInvalidDataString(String arg);

	public abstract boolean isSyntaxValid(String arg);

	public abstract T getObjectFromArg(String arg, CommandSender sender);

	public abstract String toHumanString();
}
