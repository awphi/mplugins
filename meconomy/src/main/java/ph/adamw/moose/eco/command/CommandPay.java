package ph.adamw.moose.eco.command;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ph.adamw.moose.eco.MEconomy;
import ph.adamw.moose.util.chat.ChatUtils;
import ph.adamw.moose.util.command.CommandWrapper;
import ph.adamw.moose.util.command.CommandSyntax;

public class CommandPay extends CommandWrapper {
	public CommandPay() {
		super("pay", new CommandSyntax[] {
				new CommandSyntax("[offlineplayer] [double]"),
				new CommandSyntax("[double] [offlineplayer]")
		});
	}

	@Override
	public void commandSuccessful(int syntax, CommandSender sender, Command command, String label, Object[] args) {
		final OfflinePlayer to = syntax == 0 ? (Player) args[0] : (Player) args[1];
		final int amount = syntax == 0 ? (Integer) args[1] : (Integer) args[0];

		if(!(sender instanceof Player)) {
			return;
		}

		if(!MEconomy.getPlugin().getEconomyHandler().attemptTransfer((Player) sender, to, amount)) {
			ChatUtils.messageError(sender, "Low Funds!", "You don't have enough money to complete that transaction.");
		} else {
			final Player online = to.getPlayer();

			if(online == null) {
				return;
			}

			ChatUtils.messageEconomy(online, "Money Received!", "{" + ((Player) sender).getDisplayName() + "} just paid you {$" + amount + "}.");
		}
	}
}
