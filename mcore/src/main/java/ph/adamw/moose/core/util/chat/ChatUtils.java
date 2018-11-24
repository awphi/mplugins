package ph.adamw.moose.core.util.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import ph.adamw.moose.core.util.command.CommandSyntax;
import ph.adamw.moose.core.util.command.CommandWrapper;

public class ChatUtils {
	private static int CHAT_WIDTH = 52;

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
		ChatUtils.messageError(player, "Invalid Syntax!", "Try {/help " + cmdBase + "}.");
	}

	private static String generateLine(ChatColor color1, ChatColor color2, int width) {
		final StringBuilder sb = new StringBuilder();
		boolean cswitch = true;

		for(int i = 0; i < width; i ++) {
			sb.append(cswitch ? color1 : color2);
			sb.append("-");

			cswitch = !cswitch;
		}

		return sb.toString();
	}

	public static String removeColor(String str) {
		return str.replaceAll(ChatColor.COLOR_CHAR + ".", "");
	}

	public static String generateLine(ChatColor color1, ChatColor color2, String title) {
		if(title != null && !title.isEmpty()) {
			final int side = (int) (((double) CHAT_WIDTH - (double) removeColor(title).length() - 2d) / 2d);
			return generateLine(color1, color2, side) + ChatColor.WHITE + ChatColor.BOLD + "[" + ChatColor.RESET + title  + ChatColor.WHITE + ChatColor.BOLD + "]" + generateLine(color1, color2, side);
		} else {
			return generateLine(color1, color2, CHAT_WIDTH);
		}
	}

	public static void messageCommandHelp(CommandSender sender, CommandWrapper wrapper) {
		final String title = ChatColor.AQUA + "Help" + ChatColor.WHITE + ChatColor.BOLD + " > " + ChatColor.RESET + ChatColor.GRAY + ChatColor.ITALIC + wrapper.getBase() + ChatColor.RESET;
		System.out.println(title + " " + title.length() + " " + title.toCharArray().length);
		sender.sendMessage(generateLine(ChatColor.DARK_GRAY, ChatColor.DARK_AQUA, title));

		final String BASE = ChatColor.WHITE + " /" + wrapper.getBase();

		for(CommandSyntax i : wrapper.getSyntaxes()) {
			sender.sendMessage(BASE + i.toHumanString() + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + i.getHelpText());
		}

		sender.sendMessage(generateLine(ChatColor.DARK_GRAY, ChatColor.DARK_AQUA, null));
	}
}
