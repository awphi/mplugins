package ph.adamw.moose.eco.command;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ph.adamw.moose.core.util.command.CommandSyntax;
import ph.adamw.moose.core.util.command.CommandWrapper;
import ph.adamw.moose.eco.EconomyProfile;
import ph.adamw.moose.eco.MEconomy;
import ph.adamw.moose.core.util.chat.ChatUtils;

import java.util.UUID;

public class CommandBalance extends CommandWrapper {
	public CommandBalance() {
		super("balance", new CommandSyntax[] {
				new CommandSyntax("", "Check your balance."),
				new CommandSyntax("[offlineplayer]", "Check another player's balance.")
		});
	}

	@Override
	public void commandSuccessful(int syntax, CommandSender sender, Command command, String label, Object[] args) {
		final UUID uuid;

		if(syntax == 0) {
			uuid = ((Player) sender).getUniqueId();
		} else {
			uuid = ((OfflinePlayer) args[0]).getUniqueId();
		}

		final EconomyProfile profile = MEconomy.getPlugin().getEconomyHandler().getProfile(uuid);
		final String who = syntax == 0 ? "You have " : "{" + ((OfflinePlayer) args[0]).getName() + "} has ";

		ChatUtils.messageEconomy(sender, "Balance:",  who + "{$" + profile.getBalance() + "}.");
	}
}
