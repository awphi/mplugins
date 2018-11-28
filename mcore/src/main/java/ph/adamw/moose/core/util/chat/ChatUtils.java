package ph.adamw.moose.core.util.chat;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import ph.adamw.moose.core.util.command.CommandSyntax;
import ph.adamw.moose.core.util.command.CommandWrapper;

import java.util.ArrayList;
import java.util.List;

public class ChatUtils {
	private static int CHAT_WIDTH = 52;

	private static void message(CommandSender player, MessageFormat type, String header, String content) {
		player.sendMessage(type.format(header, content));
	}

	public static void messageError(CommandSender player, String header, String content) {
		message(player, MessageFormat.ERROR, header, content);
	}

	public static void messageRaw(CommandSender sender, String content) {
		message(sender, MessageFormat.RAW, "", content);
	}

	public static void messageInfo(CommandSender player, String header, String content) {
		message(player, MessageFormat.INFO, header, content);
	}

	public static void messageEconomy(CommandSender player, String header, String content) {
		message(player, MessageFormat.ECONOMY, header, content);
	}

	public static void messageInvalidSyntax(CommandSender player, String cmdBase) {
		ChatUtils.messageError(player, "Unrecognised Syntax!", "Check out {/help " + cmdBase + "}.");
	}

	public static void messageSyntaxHelp(CommandSender sender, String cmdBase, CommandSyntax assumed) {
		ChatUtils.messageError(sender, "Invalid Syntax!", "Did you mean {/" + cmdBase + assumed.toHumanString() + "}?");
	}

	public static void messageNoPerms(CommandSender sender) {
		messageError(sender, "Invalid Permissions!", "You do not have access to that feature");
	}

	private static String generateLine(ChatColor color1, ChatColor color2, int width) {
		final StringBuilder sb = new StringBuilder();
		boolean nextColor = true;

		for(int i = 0; i < width; i ++) {
			sb.append(nextColor ? color1 : color2);
			sb.append("-");

			nextColor = !nextColor;
		}

		return sb.toString();
	}

	private static String removeColor(String str) {
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

	public static void messageLine(CommandSender sender, ChatColor color1, ChatColor color2, String title) {
		sender.sendMessage(generateLine(color1, color2, title));
	}

	public static void messageDefaultLine(CommandSender sender, String title) {
		messageLine(sender, ChatColor.DARK_GRAY, ChatColor.DARK_AQUA, title);
	}

	public static void messageCommandHelp(CommandSender sender, CommandWrapper wrapper) {
		final String BASE = ChatColor.WHITE + " /" + wrapper.getBase();
		final List<String> content = new ArrayList<>();

		for(CommandSyntax i : wrapper.getSyntaxes()) {
			if(i.isOnHelp() && !i.getHelpText().isEmpty()) {
				content.add(BASE + i.toHumanString() + ChatColor.DARK_GRAY + " - " + ChatColor.GRAY + i.getHelpText());
			}
		}

		messageDefaultBlock(sender, "Help", wrapper.getBase(), content);
	}

	public static void messageDefaultBlock(CommandSender sender, String header, String subheader, List<String> content) {
		String title = ChatColor.AQUA + header + ChatColor.WHITE + ChatColor.BOLD;
		if(subheader == null || subheader.isEmpty()) {
			title += ChatColor.RESET;
		} else {
			title += " > " + ChatColor.RESET + ChatColor.GRAY + ChatColor.ITALIC + subheader + ChatColor.RESET;
		}

		messageDefaultLine(sender, title);
		for(String i : content) {
			messageRaw(sender, i);
		}

		messageDefaultLine(sender, null);
	}
}
