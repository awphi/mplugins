package ph.adamw.moose.survival.region;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import ph.adamw.moose.core.util.config.AutoSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Region extends AutoSerializable {
	/**
	 * Expects corner1 to be bottom left and corner2 to be top right for all operations
	 */
	@Getter
	private final Location corner1;

	@Getter
	private final Location corner2;

	@Getter
	private final String name;

	// rank name to uuids
	private Map<String, List<String>> rankMap = new HashMap<>();

	Region(String name, Location corner1, Location corner2) {
		this.name = name;
		this.corner1 = corner1;
		this.corner2 = corner2;

		for(RegionRank i : RegionRank.VALUES) {
			rankMap.put(i.name(), new ArrayList<>());
		}
	}

	private Region() {
		this(null, null, null);
	}

	public static Region deserialize(Map<String, Object> map) {
		return deserializeBase(Region.class, map);
	}

	Set<Chunk> getContainedChunks() {
		int chunkX = corner1.getChunk().getX();
		int chunkZ = corner1.getChunk().getZ();

		int xdiff = corner2.getChunk().getX() - chunkX;
		int zdiff = corner2.getChunk().getZ() - chunkZ;

		final Set<Chunk> result = new HashSet<>();

		for(int i = chunkX; i <= chunkX + xdiff; i ++) {
			for(int j = chunkZ; j <= chunkZ + zdiff; j ++) {
				result.add(corner1.getWorld().getChunkAt(i, j));
			}
		}

		return result;
	}

	Set<Location> getAllCorners() {
		final Set<Location> result = new HashSet<>();
		final double xdiff = Math.abs(corner1.getX() - corner2.getX());

		result.add(corner1);
		result.add(corner2);
		result.add(new Location(corner1.getWorld(), corner1.getX() + xdiff, corner1.getY(), corner1.getZ()));
		result.add(new Location(corner2.getWorld(), corner2.getX() - xdiff, corner2.getY(), corner2.getZ()));

		return result;
	}

	public List<OfflinePlayer> getRankList(RegionRank rank) {
		final List<OfflinePlayer> result = new ArrayList<>();

		for(String i : rankMap.get(rank.name())) {
			result.add(Bukkit.getOfflinePlayer(UUID.fromString(i)));
		}

		return result;
	}

	public RegionRank getRankOf(OfflinePlayer player) {
		for(RegionRank i : RegionRank.VALUES) {
			if(rankMap.get(i.name()).contains(player.getUniqueId().toString())) {
				return i;
			}
		}

		return null;
	}

	void remove(OfflinePlayer player) {
		rankMap.get(getRankOf(player).name()).remove(player.getUniqueId().toString());
	}

	boolean containsPlayer(OfflinePlayer player) {
		for(RegionRank i : RegionRank.VALUES) {
			if(rankMap.get(i.name()).contains(player.getUniqueId().toString())) {
				return true;
			}
		}

		return false;
	}

	void setRank(OfflinePlayer player, RegionRank rank) {
		for(RegionRank i : RegionRank.VALUES) {
			if(rankMap.get(i.name()).remove(player.getUniqueId().toString())) {
				break;
			}
		}

		rankMap.get(rank.name()).add(player.getUniqueId().toString());
	}

	boolean containsLocation(Location loc) {
		return loc.getZ() >= corner1.getZ() && loc.getZ() <= corner2.getZ() && loc.getX() >= corner1.getX() && loc.getX() <= corner2.getX();
	}
}
