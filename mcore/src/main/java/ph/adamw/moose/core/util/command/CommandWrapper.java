package ph.adamw.moose.core.util.command;

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import ph.adamw.moose.core.MCore;
import ph.adamw.moose.core.perms.Rank;
import ph.adamw.moose.core.perms.RankHandler;
import ph.adamw.moose.core.util.chat.ChatUtils;

import java.util.logging.Level;

public abstract class CommandWrapper implements CommandExecutor {
	@Getter
	private final String base;

	@Getter
	private final Rank rank;

	@Getter
	private final CommandSyntax[] syntaxes;

	public CommandWrapper(String base, CommandSyntax[] syntaxes) {
		this(base, syntaxes, Rank.MEMBER);
	}

	public CommandWrapper(String base, CommandSyntax[] syntaxes, Rank rank) {
		this.base = base;
		this.rank = rank;
		this.syntaxes = syntaxes;
	}

	public static void registerCommand(JavaPlugin plugin, CommandWrapper wrapper) {
		final PluginCommand command = plugin.getCommand(wrapper.base);

		if(command == null) {
			plugin.getLogger().log(Level.SEVERE, "Failed to registerMultiBlock command: " + wrapper.base + " did you add it to plugin.yml?");
		} else {
			command.setExecutor(wrapper);
			CommandRegistry.register(wrapper);
		}
	}

	private CommandSyntax getValidSyntaxPattern(String[] args) {
		for(CommandSyntax i : syntaxes) {
			if(i.syntaxMatches(args)) {
				return i;
			}
		}

		return null;
	}

	private CommandSyntax getAssumedSyntax(String[] args) {
		int winner = 0;
		CommandSyntax syntax = null;

		for(CommandSyntax i : syntaxes) {
			int c = i.getArgumentMatches(args);

			if(c > winner) {
				syntax = i;
				winner = c;
			}
		}

		return syntax;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(!RankHandler.hasPermission(sender, rank)) {
			ChatUtils.messageNoPerms(sender);
			return true;
		}

		// Zero-arg commands
		if(syntaxes == null || syntaxes.length == 0) {
			commandSuccessful(null, sender, command, label, null);
			return true;
		}

		final CommandSyntax syntax = getValidSyntaxPattern(args);

		if(syntax == null) {
			final CommandSyntax assumed = getAssumedSyntax(args);
			if(assumed != null) {
				ChatUtils.messageSyntaxHelp(sender, base, assumed);
			} else {
				ChatUtils.messageInvalidSyntax(sender, command.getName());
			}

			return true;
		}

		final String isDataValid = syntax.isDataValid(sender, args);

		if(isDataValid != null) {
			ChatUtils.messageError(sender, "Invalid Input!", isDataValid);
			return true;
		}

		commandSuccessful(syntax.toString(), sender, command, label, syntax.stringArgsToObjects(args, sender));
		return true;
	}

	public abstract void commandSuccessful(String syntax, CommandSender sender, Command command, String label, Object[] args);
}
