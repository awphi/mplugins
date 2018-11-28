package ph.adamw.moose.core.util.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config extends YamlConfiguration {
	protected final File file;
	private final String defaults;
	private final JavaPlugin plugin;

	public Config(JavaPlugin plugin, String fileName) {
		this(plugin, fileName, null);
	}

	public Config(JavaPlugin plugin, String fileName, String defaultsName) {
		this.plugin = plugin;
		this.defaults = defaultsName;
		this.file = new File(plugin.getDataFolder(), fileName);

		reload();
	}

	public void reload() {
		if (!file.exists()) {

			try {
				file.getParentFile().mkdirs();
				file.createNewFile();

			} catch (IOException exception) {
				exception.printStackTrace();
				plugin.getLogger().severe("Error while creating file " + file.getName());
			}

		}

		try {
			load(file);

			if (defaults != null) {
				InputStreamReader reader = new InputStreamReader(plugin.getResource(defaults));
				FileConfiguration defaultsConfig = YamlConfiguration.loadConfiguration(reader);

				setDefaults(defaultsConfig);
				options().copyDefaults(true);

				reader.close();
				save();
			}

		} catch (IOException | InvalidConfigurationException exception) {
			exception.printStackTrace();
			plugin.getLogger().severe("Error while loading file " + file.getName());

		}

	}

	public void save() {

		try {
			options().indent(2);
			save(file);

		} catch (IOException exception) {
			exception.printStackTrace();
			plugin.getLogger().severe("Error while saving file " + file.getName());
		}

	}
}