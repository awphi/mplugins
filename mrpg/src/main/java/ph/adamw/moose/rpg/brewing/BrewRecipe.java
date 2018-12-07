package ph.adamw.moose.rpg.brewing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import ph.adamw.moose.core.util.config.AutoSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
public class BrewRecipe {
	// Used in finding the closest recipe, all values are pretty arbitrary and its just really for the name and flavour text
	public static final BrewRecipe NULL_RECIPE = new BrewRecipe(
			ChatColor.YELLOW + "Spoiled Brew",
			"I wouldn't drink that if I were you.",
			Color.OLIVE,
			100,
			100,
			1,
			new ArrayList<>()

	);

	private final String name;
	private final String flavourText;
	private final Color color;

	private final int cookTime;
	private final int ageTime;

	private final double difficulty;

	private final List<ItemStack> ingredients;
}
