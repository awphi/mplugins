package ph.adamw.moose.core.util.multiblock;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import ph.adamw.moose.core.MCore;
import ph.adamw.moose.core.util.config.Config;

import java.util.HashSet;
import java.util.Set;

public class MultiBlockHandler {
	private final MultiBlockListener listener;

	@Getter
	private final Set<MultiBlock> multiBlocks = new HashSet<>();

	@Getter
	private final Config config = new Config(MCore.getPlugin(), "multiblocks.yml", "multiblocks.yml");

	@Getter
	private final ConfigurationSection multiblocksSection = config.getConfigurationSection("multiblocks");

	@Getter
	private final ConfigurationSection protectionSection = config.getConfigurationSection("protection");

	public MultiBlockHandler() {
		this.listener = new MultiBlockListener(this);
	}

	public void registerMultiBlock(MultiBlock multiBlock) {
		multiBlocks.add(multiBlock);
	}

	public MultiBlock buildAt(Player player, MultiBlock multiBlock, Location location) {
		final MultiBlock instance;

		try {
			instance = multiBlock.getClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}

		instance.setCoreLocation(location);
		instance.setOwner(player.getUniqueId());

		final String identifier = location.toString();
		multiblocksSection.set(identifier, instance);

		for(Location i : instance.getPattern().traversePattern(location)) {
			protectionSection.set(i.toString(), identifier);
		}

		return instance;
	}
}
