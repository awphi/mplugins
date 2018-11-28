package ph.adamw.moose.core.basic;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ph.adamw.moose.core.MCore;
import ph.adamw.moose.core.perms.Rank;
import ph.adamw.moose.core.util.chat.ChatUtils;
import ph.adamw.moose.core.util.command.CommandSyntax;
import ph.adamw.moose.core.util.command.CommandWrapper;

import java.util.ArrayList;
import java.util.List;

public class CommandRank extends CommandWrapper {
	public CommandRank() {
		super("rank", new CommandSyntax[] {
				new CommandSyntax("", "Check your own ranks."),
				new CommandSyntax("[offlineplayer]", "Check another player's ranks.")
		});
	}

	@Override
	public void commandSuccessful(String syntax, CommandSender sender, Command command, String label, Object[] args) {
		final OfflinePlayer check;

		if(syntax.equals("")) {
			if(!(sender instanceof Player)) {
				return;
			}

			check = (OfflinePlayer) sender;
		} else {
			check = (OfflinePlayer) args[0];
		}

		final List<String> list = new ArrayList<>();
		final List<Rank> ranks = MCore.getPlugin().getRankHandler().getRanks(check.getUniqueId());

		if(ranks.size() <= 0) {
			list.add(" {" + check.getName() + "} doesn't have any ranks!");
		} else {
			list.add(" {" + check.getName() + "} has the following ranks: ");

			for(Rank i : ranks) {
				list.add(ChatColor.GRAY + "  - " + i.getColor() + i.getName());
			}
		}

		ChatUtils.messageDefaultBlock(sender, "Ranks", check.getName(), list);
	}
}
