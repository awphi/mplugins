package ph.adamw.moose.core.basic;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import ph.adamw.moose.core.util.chat.ChatUtils;
import ph.adamw.moose.core.util.command.CommandSyntax;
import ph.adamw.moose.core.util.command.CommandWrapper;

public class CommandHelp extends CommandWrapper {
	public CommandHelp() {
		super("help", new CommandSyntax[] {
				new CommandSyntax("", "Access the help menu."),
				new CommandSyntax("[mcommand]", "Access the help menu of a specific command.")
		});
	}

	@Override
	public void commandSuccessful(String syntax, CommandSender sender, Command command, String label, Object[] args) {
		if(syntax.equals("")) {
			//TODO help for all commands w/ paging
		} else {
			ChatUtils.messageCommandHelp(sender, (CommandWrapper) args[0]);
		}
	}
}
