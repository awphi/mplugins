package ph.adamw.moose.rpg.fish;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import ph.adamw.moose.rpg.MRpg;
import ph.adamw.moose.core.util.enchant.EnchantWrapper;

public class FishermansDelightWrapper extends EnchantWrapper {
	public FishermansDelightWrapper() {
		super(MRpg.getPlugin(), "fishermansdelight");
	}

	@Override
	public String getName() {
		return "Fisherman's Delight";
	}

	@Override
	public int getMaxLevel() {
		return 1;
	}

	@Override
	public int getStartLevel() {
		return 1;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.FISHING_ROD;
	}

	@Override
	public boolean isTreasure() {
		return false;
	}

	@Override
	public boolean isCursed() {
		return false;
	}

	@Override
	public boolean conflictsWith(Enchantment enchantment) {
		return false;
	}

	@Override
	public boolean canEnchantItem(ItemStack itemStack) {
		return itemStack.getType() == Material.FISHING_ROD;
	}

	@Override
	public boolean isAvailableOnTable() {
		return true;
	}

	@Override
	public float getTableChance() {
		return 1f;
	}

	@Override
	public int getTableLevel() {
		return 2;
	}
}
