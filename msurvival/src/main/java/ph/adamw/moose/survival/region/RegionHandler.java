package ph.adamw.moose.survival.region;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ph.adamw.moose.core.util.command.CommandArgument;
import ph.adamw.moose.core.util.config.Config;
import ph.adamw.moose.core.util.chat.ChatUtils;
import ph.adamw.moose.survival.MSurvival;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RegionHandler {
	private Config config = new Config(MSurvival.getPlugin(), "regions.yml", "regions.yml");

	private static final String CHUNKS_SECTION = "chunks";
	private static final String REG_SECTION = "registry";

	private final Map<UUID, Location[]> locationMap = new HashMap<>();

	private final RegionListener listener = new RegionListener(this);

	static {
		CommandArgument.register(new CommandArgument<Region>("[region]") {
			@Override
			public String getInvalidDataString(String arg) {
				return "There is not a region called {" + arg + "}.";
			}

			@Override
			public boolean isSyntaxValid(String arg) {
				return !arg.isEmpty();
			}

			@Override
			public Region getObjectFromArg(String arg, CommandSender sender) {
				return MSurvival.getPlugin().getRegionHandler().getRegion(arg);
			}

			@Override
			public String toHumanString() {
				return "region name";
			}
		});
	}

	public Region registerRegion(String name, Location corner1, Location corner2) {
		final Region region = coordinateRegion(name, corner1, corner2);
		config.getConfigurationSection(REG_SECTION).set(region.getName(), region);

		for(Chunk i : region.getContainedChunks()) {
			if(!config.getConfigurationSection(CHUNKS_SECTION).contains(getChunkId(i))) {
				config.getConfigurationSection(CHUNKS_SECTION).set(getChunkId(i), new ArrayList<>(Collections.singleton(region.getName())));
			} else {
				getRegionNames(i).add(region.getName());
			}
		}

		return region;
	}

	public void deregisterRegion(Region region) {
		config.getConfigurationSection(REG_SECTION).set(region.getName(), null);

		for(Chunk i : region.getContainedChunks()) {
			if(config.getConfigurationSection(CHUNKS_SECTION).contains(getChunkId(i))) {
				getRegionNames(i).remove(region.getName());
			}
		}
	}

	void defineCorner(boolean zero, Player player, Location location) {
		final UUID uuid = player.getUniqueId();

		if(!locationMap.containsKey(uuid)) {
			locationMap.put(uuid, new Location[2]);
		}

		final Location[] list = locationMap.get(uuid);
		final int c = zero ? 0 : 1;

		list[c] = location;
		ChatUtils.messageInfo(player, "Set Corner " + (c + 1) + "!", "Region corner defined at: {" + location.toVector().toString() + "}.");
	}

	Location[] getCorners(Player sender) {
		return locationMap.get(sender.getUniqueId());
	}

	private Region coordinateRegion(String name, Location corner1, Location corner2) {
		final Location topCorner;
		final Location bottomCorner;

		if(corner1.getX() > corner2.getX() || corner1.getZ() > corner2.getZ()) {
			topCorner = corner1;
			bottomCorner = corner2;
		} else {
			topCorner = corner2;
			bottomCorner = corner1;
		}

		final boolean topAlignedLeft = topCorner.getX() < bottomCorner.getX();

		if(topAlignedLeft) {
			final double xdiff = Math.abs(bottomCorner.getX() - topCorner.getX());
			return new Region(
					name,
					new Location(bottomCorner.getWorld(), bottomCorner.getX() - xdiff, bottomCorner.getY(), bottomCorner.getZ()),
					new Location(topCorner.getWorld(), topCorner.getX() + xdiff, topCorner.getY(), topCorner.getZ())
			);
		} else {
			return new Region(name, bottomCorner, topCorner);
		}
	}

	private String getChunkId(Chunk chunk) {
		return chunk.getWorld().getName() + "_(" + chunk.getZ() + "|" + chunk.getX() + ")";
	}

	public Region getRegion(Location loc) {
		if(config.getConfigurationSection(CHUNKS_SECTION).contains(getChunkId(loc.getChunk()))) {
			final List<String> list = getRegionNames(loc.getChunk());
			for(String i : list) {
				final Region rg = ((Region) config.getConfigurationSection(REG_SECTION).get(i));
				if(rg.containsLocation(loc)) {
					return rg;
				}
			}
		}

		return null;
	}

	public Region getRegion(String name) {
		if(config.getConfigurationSection(REG_SECTION).contains(name)) {
			return (Region) config.getConfigurationSection(REG_SECTION).get(name);
		}

		return null;
	}

	private List<String> getRegionNames(Chunk i) {
		return (List<String>) config.getConfigurationSection(CHUNKS_SECTION).get(getChunkId(i));
	}

	public boolean wouldRegionOverlapAnyOthers(Location corner1, Location corner2) {
		final Region dummy = coordinateRegion("dummy", corner1, corner2);
		for(Chunk i : dummy.getContainedChunks()) {
			final List<String> regionNames = getRegionNames(i);
			if(regionNames == null || regionNames.size() <= 0) {
				continue;
			}

			for(String j : regionNames) {
				if(doRegionsOverlap(dummy, getRegion(j))) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean doRegionsOverlap(Region dummy, Region region) {
		for(Location i : dummy.getAllCorners()) {
			if(region.containsLocation(i)) {
				return true;
			}
		}

		for(Location i : region.getAllCorners()) {
			if(dummy.containsLocation(i)) {
				return true;
			}
		}

		return false;
	}
}
