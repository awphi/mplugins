package ph.adamw.moose.core.util;

import lombok.NoArgsConstructor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

@NoArgsConstructor
public abstract class AutoSerializable implements ConfigurationSerializable {
	public AutoSerializable(Map<String, Object> map) {
		for(Field i : this.getClass().getDeclaredFields()) {
			i.setAccessible(true);
			try {
				i.set(this, map.get(i.getName()));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Map<String, Object> serialize() {
		final Map<String, Object> result = new LinkedHashMap<>();

		for(Field i : this.getClass().getDeclaredFields()) {
			i.setAccessible(true);

			if(!Modifier.isTransient(i.getModifiers())) {
				try {
					result.put(i.getName(), i.get(this));
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}
}
