package ph.adamw.moose.eco.command;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ph.adamw.moose.eco.MEconomy;
import ph.adamw.moose.core.util.chat.ChatUtils;
import ph.adamw.moose.core.util.command.CommandWrapper;
import ph.adamw.moose.core.util.command.CommandSyntax;

public class CommandPay extends CommandWrapper {
	public CommandPay() {
		super("pay", new CommandSyntax[] {
				new CommandSyntax("[offlineplayer] [double]", "Pay another player a sum of money.")
		});
	}

	@Override
	public void commandSuccessful(String syntax, CommandSender sender, Command command, String label, Object[] args) {
		final OfflinePlayer to = (OfflinePlayer) args[0];
		final double amount = (double) args[1];

		if(!(sender instanceof Player)) {
			return;
		}

		if(!MEconomy.getPlugin().getEconomyHandler().attemptTransfer((Player) sender, to, amount)) {
			ChatUtils.messageError(sender, "Low Funds!", "You don't have enough money to complete that transaction.");
		} else {
			if(to.isOnline()) {
				ChatUtils.messageEconomy(to.getPlayer(), "Payment Received!", "{" + ((Player) sender).getDisplayName() + "} just paid you {$" + amount + "}.");
			} else {
				//TODO mail
			}
		}
	}
}
