package ph.adamw.moose.core.util.command;

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import ph.adamw.moose.core.MCore;
import ph.adamw.moose.core.util.chat.ChatUtils;

import java.util.logging.Level;

public abstract class CommandWrapper implements CommandExecutor {
	@Getter
	private final String base;

	@Getter
	private final CommandSyntax[] syntaxes;

	public CommandWrapper(String base, CommandSyntax[] syntaxes) {
		this.base = base;
		this.syntaxes = syntaxes;
	}

	public static void registerCommand(JavaPlugin plugin, CommandWrapper wrapper) {
		final PluginCommand command = plugin.getCommand(wrapper.base);

		if(command == null) {
			plugin.getLogger().log(Level.SEVERE, "Failed to register command " + wrapper.base + " did you add it to plugin.yml?");
		} else {
			command.setExecutor(wrapper);
			MCore.getPlugin().getCommandRegistry().register(wrapper);
		}
	}

	private int indexOfSyntax(CommandSyntax syntax) {
		for(int i = 0; i < syntaxes.length; i ++) {
			if(syntaxes[i] == syntax) {
				return i;
			}
		}

		return - 1;
	}

	private CommandSyntax getValidSyntaxPattern(String[] args) {
		for(CommandSyntax i : syntaxes) {
			if(i.syntaxMatches(args)) {
				return i;
			}
		}

		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// Zero-arg commands
		if(syntaxes == null || syntaxes.length == 0) {
			commandSuccessful(-1, sender, command, label, null);
			return true;
		}

		final CommandSyntax syntax = getValidSyntaxPattern(args);

		if(syntax == null) {
			ChatUtils.messageInvalidSyntax(sender, command.getName());
			return true;
		}

		final String isDataValid = syntax.isDataValid(args);

		if(isDataValid != null) {
			ChatUtils.messageError(sender, "Invalid Input!", isDataValid);
			return true;
		}

		commandSuccessful(indexOfSyntax(syntax), sender, command, label, syntax.stringArgsToObjects(args));
		return true;
	}

	public abstract void commandSuccessful(int syntax, CommandSender sender, Command command, String label, Object[] args);
}
