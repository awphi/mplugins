package ph.adamw.moose.core.util.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
public abstract class AutoSerializable implements ConfigurationSerializable {
	private static List<Field> getFields(Class clazz) {
		List<Field> fields = new ArrayList<>();

		while (clazz != Object.class) {
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
			clazz = clazz.getSuperclass();
		}

		return fields;
	}

	protected static <T extends AutoSerializable> T deserializeBase(Class<T> clazz, Map<String, Object> map) {
		final T inst;
		try {
			final Constructor<T> cs = clazz.getDeclaredConstructor();
			cs.setAccessible(true);
			inst = cs.newInstance();
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}

		for(Field i : getFields(clazz)) {
			i.setAccessible(true);

			try {
				if(map.containsKey(i.getName())) {
					i.set(inst, map.get(i.getName()));
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return inst;
	}

	@Override
	public Map<String, Object> serialize() {
		final Map<String, Object> result = new LinkedHashMap<>();

		for(Field i : getFields(this.getClass())) {
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
