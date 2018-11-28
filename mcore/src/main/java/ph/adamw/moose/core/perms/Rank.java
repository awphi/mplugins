package ph.adamw.moose.core.perms;

import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public enum Rank {
	ADMIN(0, 0, ChatColor.RED, "Administrator"),
	MOD(0, 1, ChatColor.AQUA, "Moderator"),

	DEVELOPER(1, 0, ChatColor.LIGHT_PURPLE, "Developer"),

	PRO(2, 0, ChatColor.GREEN, "Pro"),
	MVP(2, 1, ChatColor.YELLOW, "MVP"),
	SUPPORTER(2, 2, ChatColor.GOLD, "Supporter"),

	MEMBER(3, 0, ChatColor.DARK_GRAY, "Member");

	private final int groupId;
	private final int groupLevel;

	@Getter
	private final ChatColor color;

	@Getter
	private final String name;

	Rank(int groupId, int groupLevel, ChatColor color, String name) {
		this.groupId = groupId;
		this.groupLevel = groupLevel;
		this.color = color;
		this.name = name;
	}

	public static Rank getHighestPriority(List<Rank> list) {
		if(list.size() <= 0) {
			return null;
		}

		Rank result = list.get(0);

		for(Rank i : list) {
			if(i.groupId <= result.groupId && i.groupLevel <= result.groupLevel) {
				result = i;
			}
		}

		return result;
	}

	public static boolean hasPermissions(List<Rank> ranks, Rank desired) {
		for(Rank i : ranks) {
			if(i.groupId == desired.groupId && i.groupLevel <= desired.groupLevel) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String toString() {
		return name;
	}
}
