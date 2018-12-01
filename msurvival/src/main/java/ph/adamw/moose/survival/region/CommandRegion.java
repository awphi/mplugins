package ph.adamw.moose.survival.region;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ph.adamw.moose.core.MCore;
import ph.adamw.moose.core.perms.RankHandler;
import ph.adamw.moose.core.util.chat.ChatUtils;
import ph.adamw.moose.core.util.command.CommandSyntax;
import ph.adamw.moose.core.util.command.CommandWrapper;
import ph.adamw.moose.survival.MSurvival;

import java.util.ArrayList;
import java.util.List;

public class CommandRegion extends CommandWrapper {
	private final RegionHandler regionHandler = MSurvival.getPlugin().getRegionHandler();
	private final RankHandler rankHandler = MCore.getPlugin().getRankHandler();

	public CommandRegion() {
		super("region", new CommandSyntax[] {
				new CommandSyntax("info", "View information on your current region."),
				new CommandSyntax("info [region]", "View information on a given region."),
				new CommandSyntax("list", "View all of your regions."),
				new CommandSyntax("list [integer]", "View a page of your region list.", false),

				new CommandSyntax("create [string]", "Create a new region using your defined corners."),

				new CommandSyntax("delete", "Delete your current region."),
				new CommandSyntax("delete [region]", "Delete one of your regions."),

				new CommandSyntax("add [offlineplayer]", "Add a player as to your region."),
				new CommandSyntax("remove [offlineplayer]", "Remove a player from your region."),
				new CommandSyntax("promote [offlineplayer]", "Promote a player within your region."),
				new CommandSyntax("demote [offlineplayer]", "Demote a player within your region.")
		});
	}

	@Override
	public void commandSuccessful(String syntax, CommandSender sender, Command command, String label, Object[] args) {
		if (!(sender instanceof Player)) {
			ChatUtils.messageError(sender, "Players Only!", "This command can only be executed by players!");
			return;
		}

		final Region currentRegion = regionHandler.getRegion(((Player) sender).getLocation());

		switch (syntax) {
			case "info":
			case "info [region]": info(sender, args.length == 1 ?  currentRegion : (Region) args[1]); break;
			case "list":
			case "list [integer]": list((Player) sender, args.length == 1 ? 1 : (int) args[1]); break;

			case "create [string]": create((Player) sender, (String) args[1]); break;
			case "delete":
			case "delete [region]": delete((Player) sender, args.length == 1 ? currentRegion : (Region) args[1]); break;

			case "add [offlineplayer]": add((Player) sender, currentRegion, (OfflinePlayer) args[1]); break;
			case "remove [offlineplayer]": remove((Player) sender, currentRegion, (OfflinePlayer) args[1]); break;
			case "promote [offlineplayer]": promote((Player) sender, currentRegion, (OfflinePlayer) args[1]); break;
			case "demote [offlineplayer]": demote((Player) sender, currentRegion, (OfflinePlayer) args[1]); break;
		}
	}

	private boolean isAdminAndInRegion(Region region, Player sender) {
		if(region == null) {
			messageNoRegion(sender);
			return false;
		}

		if(!region.containsPlayer(sender) || !region.getRankOf(sender).canPerform(RegionRank.ADMIN)) {
			messageNoPerms(sender, RegionRank.ADMIN);
			return false;
		}

		return true;
	}

	private boolean rankControlCheck(Region region, Player sender, OfflinePlayer player) {
		if(!isAdminAndInRegion(region, sender)) {
			return false;
		}

		if(!region.containsPlayer(player)) {
			ChatUtils.messageError(sender, "Invalid Player!", "That player does not have access to the region, please add them first.");
			return false;
		}

		if(!region.getRankOf(sender).canPerform(RegionRank.OWNER) && region.getRankOf(player).canPerform(RegionRank.ADMIN)) {
			ChatUtils.messageError(sender, "Invalid Player!", "You need to be a region owner to do that.");
			return false;
		}

		return true;
	}

	private void demote(Player sender, Region region, OfflinePlayer player) {
		if(!rankControlCheck(region, sender, player)) {
			return;
		}

		final RegionRank rank = region.getRankOf(player).nextDown();
		final boolean canDemote = rank == region.getRankOf(player);
		if(canDemote) {
			region.setRank(player, rank);
			ChatUtils.messageInfo(sender, "Demoted Player!", "{" + player.getName() + "}'s now has {" + rank.getName() + "} access to the region.");
		} else {
			ChatUtils.messageError(sender, "Demotion Error!", "{" + player.getName() + "} already has the lowest region rank.");
		}
	}

	private void promote(Player sender, Region region, OfflinePlayer player) {
		if(!rankControlCheck(region, sender, player)) {
			return;
		}

		final RegionRank rank = region.getRankOf(player).nextUp();
		final boolean canPromote = rank == region.getRankOf(player);

		if(canPromote) {
			region.setRank(player, rank);
			ChatUtils.messageInfo(sender, "Demoted Player!", "{" + player.getName() + "}'s now has {" + rank.getName() + "} access to the region.");
		} else {
			ChatUtils.messageError(sender, "Demotion Error!", "{" + player.getName() + "} already has the highest region rank.");
		}
	}

	private void remove(Player sender, Region region, OfflinePlayer player) {
		if(!isAdminAndInRegion(region, sender)) {
			return;
		}

		if(!region.containsPlayer(player)) {
			ChatUtils.messageError(sender, "Invalid Player!", "That player does not have access to the region already.");
			return;
		}

		if(region.getRankOf(player).canPerform(RegionRank.OWNER)) {
			region.remove(player);
		} else {
			// Make sure the player the admin is trying to remove is not an admin or owner
			if(!region.getRankOf(player).canPerform(RegionRank.ADMIN)) {
				region.remove(player);
			} else {
				ChatUtils.messageError(sender, "Invalid Player!", "You cannot remove an admin or a region owner unless you are region owner!");
				return;
			}
		}

		ChatUtils.messageInfo(sender, "Removed Player!", "{" + player.getName() + "}'s access to the region has been revoked.");
	}

	private void add(Player sender, Region region, OfflinePlayer player) {
		if(!isAdminAndInRegion(region, sender)) {
			return;
		}

		if(region.containsPlayer(player)) {
			ChatUtils.messageError(sender, "Invalid Player!", "That player is already added to the region!");
			return;
		}

		ChatUtils.messageInfo(sender, "Added Player!", "{" + player.getName() + "} now has " + RegionRank.VISITOR.getName() + " access to the region!");
		region.setRank(player, RegionRank.VISITOR);
	}

	private void list(Player sender, int page) {
		// TODO
	}

	private void delete(Player sender, Region region) {
		if(!region.containsPlayer(sender) || !region.getRankOf(sender).canPerform(RegionRank.OWNER)) {
			messageNoPerms(sender, RegionRank.OWNER);
			return;
		}

		regionHandler.deregisterRegion(region);
		ChatUtils.messageInfo(sender, "Deleted Region!", "Successfully deleted region: {" + region.getName() + "}.");
	}

	private void create(Player sender, String name) {
		final Location[] corners = regionHandler.getCorners(sender);
		if(corners == null || corners[0] == null || corners[1] == null) {
			ChatUtils.messageError(sender, "Invalid Region!", "You must define two corners with a stick first.");
			return;
		}

		if(regionHandler.wouldRegionOverlapAnyOthers(corners[0], corners[1])) {
			ChatUtils.messageError(sender,"Invalid Region!", "Your new region cannot overlap an existing region.");
			return;
		}

		if(regionHandler.getRegion(name) != null) {
			ChatUtils.messageError(sender,"Invalid Region!", "There is already a region with the name {" + name + "}.");
			return;
		}

		final Region rg = regionHandler.registerRegion(name, corners[0], corners[1]);
		rg.setRank(sender, RegionRank.OWNER);

		ChatUtils.messageInfo(sender, "Region Created!", "Registered new region: {" + name + "}.");
	}

	private void info(CommandSender sender, Region region) {
		if(region == null) {
			messageNoRegion(sender);
			return;
		}

		final List<String> info = new ArrayList<>();
		info.add(ChatColor.GRAY + " Region: {" + region.getName() + "}");
		for(RegionRank i : RegionRank.VALUES) {
			info.add(ChatColor.GRAY + " " + i.getName() + "s:");

			for (OfflinePlayer j : region.getRankList(i)) {
				info.add(ChatColor.GRAY + "  - " + rankHandler.getFormattedName(j));
			}
		}

		ChatUtils.messageDefaultBlock(sender, "Regions", region.getName(), info);
	}

	private void messageNoPerms(CommandSender sender, RegionRank required) {
		ChatUtils.messageError(sender, "No Permissions!", "You must have the region rank of {" + required.getName() + "} to do that.");
	}

	private void messageNoRegion(CommandSender sender) {
		ChatUtils.messageError(sender, "No Region!", "You are not currently standing in a region.");
	}
}
