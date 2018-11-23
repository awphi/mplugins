package ph.adamw.moose.eco.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ph.adamw.moose.eco.MEconomy;
import ph.adamw.moose.util.command.CommandSyntax;
import ph.adamw.moose.util.command.CommandWrapper;

public class CommandVault extends CommandWrapper {
	public CommandVault() {
		super("vault", new CommandSyntax[] {
				new CommandSyntax("")
		});
	}

	@Override
	public void commandSuccessful(int syntax, CommandSender sender, Command command, String label, Object[] args) {
		if(!(sender instanceof Player)) {
			return;
		}

		final Player p = (Player) sender;

		MEconomy.getPlugin().getEconomyHandler().getProfile(p.getUniqueId()).openVault(p);
	}
}
