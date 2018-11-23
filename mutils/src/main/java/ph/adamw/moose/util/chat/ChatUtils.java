package ph.adamw.moose.util.chat;

import org.bukkit.command.CommandSender;
import ph.adamw.moose.util.command.CommandWrapper;

public class ChatUtils {
	public static void message(CommandSender player, MessageFormat type, String header, String content) {
		player.sendMessage(type.format(header, content));
	}

	public static void messageError(CommandSender player, String header, String content) {
		message(player, MessageFormat.ERROR, header, content);
	}

	public static void messageInfo(CommandSender player, String header, String content) {
		message(player, MessageFormat.INFO, header, content);
	}

	public static void messageEconomy(CommandSender player, String header, String content) {
		message(player, MessageFormat.ECONOMY, header, content);
	}

	public static void messageInvalidSyntax(CommandSender player, String cmdBase) {
		ChatUtils.messageError(player, "Invalid Syntax!", "Try {/" + cmdBase + " help}.");
	}

	public static void messageCommandHelp(CommandSender sender, CommandWrapper commandWrapper) {
		sender.sendMessage("Not done yet - implement auto generated help menus soon!");
		//TODO Help menu generator using possible syntaxes + their descriptions
	}
}
