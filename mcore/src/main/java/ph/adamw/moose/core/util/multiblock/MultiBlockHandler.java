package ph.adamw.moose.core.util.multiblock;

import com.google.common.collect.ImmutableSet;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import ph.adamw.moose.core.MCore;
import ph.adamw.moose.core.util.MPlugin;
import ph.adamw.moose.core.util.config.Config;
import ph.adamw.moose.core.util.multiblock.pattern.MultiBlockPattern;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MultiBlockHandler {
	private static final MultiBlockListener listener = new MultiBlockListener();

	private static final HashMap<Class<? extends MultiBlock>, Config> configMap = new HashMap<>();
	private static final HashMap<Class<? extends MultiBlock>, MultiBlockPattern> patternMap = new HashMap<>();

	private static final String REGISTRY_STRING = "multiblocks";
	private static final String PROTECTION_STRING = "protection";



	public static void registerMultiBlock(MPlugin plugin, Class<? extends MultiBlock> multiBlock, MultiBlockPattern pattern) {
		if(!configMap.containsKey(multiBlock)) {
			final Config config = new Config(plugin, multiBlock.getSimpleName().toLowerCase() + ".yml");
			config.createSection(REGISTRY_STRING);
			config.createSection(PROTECTION_STRING);
			configMap.put(multiBlock, config);

			patternMap.put(multiBlock, pattern);
		}
	}

	static MultiBlock constructNew(Player player, Class<? extends MultiBlock> multiBlock, Location location) {
		final MultiBlock instance;

		try {
			instance = multiBlock.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}

		final Config config = configMap.get(multiBlock);
		final ConfigurationSection protectionSection = config.getConfigurationSection(PROTECTION_STRING);
		final ConfigurationSection multiblocksSection = config.getConfigurationSection(REGISTRY_STRING);

		instance.setCoreLocation(location);
		instance.setOwner(Bukkit.getOfflinePlayer(player.getUniqueId()));

		final String identifier = location.toString();
		multiblocksSection.set(identifier, instance);

		for(Location i : MultiBlockHandler.getPattern(instance.getClass()).traversePattern(location)) {
			protectionSection.set(i.toString(), identifier);
		}

		instance.onCreate(player);

		return instance;
	}

	static void delete(MultiBlock multiBlock) {
		final Config config = configMap.get(multiBlock.getClass());
		final ConfigurationSection protectionSection = config.getConfigurationSection(PROTECTION_STRING);
		final ConfigurationSection multiblocksSection = config.getConfigurationSection(REGISTRY_STRING);

		// Prune the configs of this multiblock
		for(Location i :  MultiBlockHandler.getPattern(multiBlock.getClass()).traversePattern(multiBlock.getCoreLocation())) {
			protectionSection.set(i.toString(), null);
		}

		multiblocksSection.set(multiBlock.getCoreLocation().toString(), null);
	}

	static MultiBlock getMultiBlockFromElement(Block block) {
		final String blockLocation = block.getLocation().toString();

		for(Config i : configMap.values()) {
			final ConfigurationSection protectionSection = i.getConfigurationSection(PROTECTION_STRING);
			final ConfigurationSection multiblocksSection = i.getConfigurationSection(REGISTRY_STRING);

			if(protectionSection.contains(blockLocation)) {
				final String coreLocation = (String) protectionSection.get(blockLocation);

				if (multiblocksSection.contains(coreLocation)) {
					return (MultiBlock) multiblocksSection.get(coreLocation);
				}
			}
		}

		return null;
	}

	static MultiBlock getMultiBlockFromCore(Block block) {
		final String str = block.getLocation().toString();

		for(Config i : configMap.values()) {
			final ConfigurationSection multiblocksSection = i.getConfigurationSection(REGISTRY_STRING);

			if(multiblocksSection.contains(str)) {
				return (MultiBlock) multiblocksSection.get(str);
			}
		}

		return null;
	}

	public static MultiBlock getMultiBlock(Block block) {
		final MultiBlock x = MultiBlockHandler.getMultiBlockFromElement(block);
		return x == null ? MultiBlockHandler.getMultiBlockFromCore(block) : x;
	}

	public static MultiBlockPattern getPattern(Class<? extends MultiBlock> clazz) {
		return patternMap.get(clazz);
	}

	public static Set<Class<? extends MultiBlock>> getMultiBlocks() {
		return ImmutableSet.copyOf(patternMap.keySet());
	}
}
