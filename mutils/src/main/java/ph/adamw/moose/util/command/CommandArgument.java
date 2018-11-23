package ph.adamw.moose.util.command;

import com.google.common.collect.ImmutableSet;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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
				public Player getObjectFromArg(String arg) {
					for(Player i : Bukkit.getOnlinePlayers()) {
						if(i.getName().equals(arg)) {
							return i;
						}
					}

					return null;
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
				public OfflinePlayer getObjectFromArg(String arg) {
					for(OfflinePlayer i : Bukkit.getOfflinePlayers()) {
						if(i.getName().equals(arg)) {
							return i;
						}
					}

					return null;
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
				public Integer getObjectFromArg(String arg) {
					return Integer.valueOf(arg);
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
				public Double getObjectFromArg(String arg) {
					return Double.valueOf(arg);
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

	public abstract T getObjectFromArg(String arg);
}
