package ph.adamw.moose.core.util.command;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import ph.adamw.moose.core.MCore;

import java.util.Set;

public abstract class CommandArgument<T> {
	private final static Set<CommandArgument> SET = new ImmutableSet.Builder<CommandArgument>()
			.add(new CommandArgument<Player>("[player]") {
				@Override
				public String getInvalidDataString(String arg) {
					return "{" + arg + "} isn't currently online.";
				}

				@Override
				public boolean isSyntaxValid(String arg) {
					return !arg.isEmpty();
				}

				@Override
				public Player getObjectFromArg(String arg, Player player) {
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
			})
			.add(new CommandArgument<OfflinePlayer>("[offlineplayer]") {
				@Override
				public String getInvalidDataString(String arg) {
					return "{" + arg + "} has never logged on.";
				}

				@Override
				public boolean isSyntaxValid(String arg) {
					return !arg.isEmpty();
				}

				@Override
				public OfflinePlayer getObjectFromArg(String arg, Player player) {
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
			})
			.add(new CommandArgument<Integer>("[integer]") {
				@Override
				public String getInvalidDataString(String arg) {
					return "{" + arg + "} is not an integer.";
				}

				@Override
				public boolean isSyntaxValid(String arg) {
					return arg.matches("[1-9][0-9]*");
				}

				@Override
				public Integer getObjectFromArg(String arg, Player player) {
					return Integer.valueOf(arg);
				}

				@Override
				public String toHumanString() {
					return "integer";
				}
			})
			.add(new CommandArgument<Double>("[double]") {
				@Override
				public String getInvalidDataString(String arg) {
					return "{" + arg + "} is not a rational number.";
				}

				@Override
				public boolean isSyntaxValid(String arg) {
					return arg.matches("[1-9][0-9]*(.[0-9]+)?");
				}

				@Override
				public Double getObjectFromArg(String arg, Player player) {
					return Double.valueOf(arg);
				}

				@Override
				public String toHumanString() {
					return "decimal";
				}
			})
			.add(new CommandArgument<CommandWrapper>("[mcommand]") {
				@Override
				public String getInvalidDataString(String arg) {
					return "{" + arg + "} is not a recognised command on this server.";
				}

				@Override
				public boolean isSyntaxValid(String arg) {
					return !arg.isEmpty();
				}

				@Override
				public CommandWrapper getObjectFromArg(String arg, Player player) {
					return MCore.getPlugin().getCommandRegistry().getWrapper(arg);
				}

				@Override
				public String toHumanString() {
					return "command";
				}
			})
			.build();

	public static CommandArgument fromPattern(String pattern) {
		for(CommandArgument i : SET) {
			if(i.pattern.equalsIgnoreCase(pattern)) {
				return i;
			}
		}

		throw new RuntimeException("Unknown command argument " + pattern + ", is this a typo or did you not register it?");
	}

	private final String pattern;

	protected CommandArgument(String pattern) {
		this.pattern = pattern;
	}

	public abstract String getInvalidDataString(String arg);

	public abstract boolean isSyntaxValid(String arg);

	public abstract T getObjectFromArg(String arg, Player player);

	public abstract String toHumanString();
}
