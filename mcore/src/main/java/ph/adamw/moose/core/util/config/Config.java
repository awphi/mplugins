package ph.adamw.moose.core.util.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import ph.adamw.moose.core.MCore;
import ph.adamw.moose.core.util.MPlugin;

public class Config extends YamlConfiguration {
	protected final File file;
	private final String defaults;
	private final JavaPlugin plugin;

	@Getter
	private final boolean autoSave;


	public Config(MPlugin plugin, String fileName, String defaults) {
		this(plugin, fileName, defaults, true);
	}

	public Config(MPlugin plugin, String fileName) {
		this(plugin, fileName, null, true);
	}

	public Config(MPlugin plugin, String fileName, boolean autoSave) {
		this(plugin, fileName, null, autoSave);
	}

	public Config(MPlugin plugin, String fileName, String defaultsName, boolean autoSave) {
		this.plugin = plugin;
		this.defaults = defaultsName;
		this.file = new File(plugin.getDataFolder(), fileName);
		this.autoSave = autoSave;

		ConfigRegistry.register(plugin, this);

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
			options().indent(4);
			save(file);

		} catch (IOException exception) {
			exception.printStackTrace();
			plugin.getLogger().severe("Error while saving file " + file.getName());
		}
	}
}