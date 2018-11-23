package ph.adamw.moose.util.chat;

import org.bukkit.ChatColor;

public enum MessageFormat {
	INFO("• ", "|header| |content|", ChatColor.DARK_AQUA, ChatColor.AQUA, ChatColor.GRAY, ChatColor.WHITE),
	ERROR("• ", "|header| |content|", ChatColor.DARK_RED, ChatColor.RED, ChatColor.GRAY, ChatColor.WHITE),
	ECONOMY("• ", "|header| |content|", ChatColor.DARK_GREEN, ChatColor.GREEN, ChatColor.GRAY, ChatColor.WHITE);

	private final String icon;
	private final String format;

	private final ChatColor iconColor;
	private final ChatColor headerColor;
	private final ChatColor contentColor;
	private final ChatColor emphasisColor;

	MessageFormat(String icon, String format, ChatColor iconColor, ChatColor headerColor, ChatColor contentColor, ChatColor emphasisColor) {
		this.icon = icon;
		this.format = format;
		this.emphasisColor = emphasisColor;
		this.iconColor = iconColor;
		this.headerColor = headerColor;
		this.contentColor = contentColor;
	}

	public String format(String header, String content) {
		content = content.replaceAll("\\{", emphasisColor.toString());
		content = content.replaceAll("}", contentColor.toString());

		String x = iconColor + icon + format;
		x = x.replace("|header|", headerColor + header);
		x = x.replace("|content|", contentColor + content);

		return x;
	}
}
