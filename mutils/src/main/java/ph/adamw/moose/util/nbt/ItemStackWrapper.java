package ph.adamw.moose.util.nbt;

import lombok.Getter;
import net.minecraft.server.v1_13_R2.NBTBase;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import net.minecraft.server.v1_13_R2.NBTTagFloat;
import net.minecraft.server.v1_13_R2.NBTTagInt;
import net.minecraft.server.v1_13_R2.NBTTagString;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public class ItemStackWrapper {
	@Getter
	private ItemStack itemStack;

	private ItemMeta meta;
	private NBTTagCompound compound;

	public ItemStackWrapper(ItemStack itemStack) {
		this.itemStack = itemStack;
		meta = itemStack.getItemMeta();
		net.minecraft.server.v1_13_R2.ItemStack nmsStack = asNMS();
		compound = nmsStack.hasTag() ? nmsStack.getTag() : new NBTTagCompound();
	}

	public ItemStackWrapper(Material item, int amount) {
		this(new ItemStack(item, amount));
	}

	/* --- Meta --- */

	public void setName(String s) {
		meta.setDisplayName(s);
		itemStack.setItemMeta(meta);
	}

	public void setLore(String... in) {
		meta.setLore(new ArrayList<>(Arrays.asList(in)));
		itemStack.setItemMeta(meta);
	}

	/* --- NBT --- */

	private net.minecraft.server.v1_13_R2.ItemStack asNMS() {
		return CraftItemStack.asNMSCopy(itemStack);
	}

	private void setTag(String tag, NBTBase in) {
		net.minecraft.server.v1_13_R2.ItemStack nms = asNMS();
		compound.set(tag, in);
		nms.setTag(compound);
		itemStack = CraftItemStack.asBukkitCopy(nms);
	}

	public void setInt(String tag, int in) {
		setTag(tag, new NBTTagInt(in));
	}

	public void setString(String tag, String in) {
		setTag(tag, new NBTTagString(in));
	}

	public void setFloat(String tag, float in) {
		setTag(tag, new NBTTagFloat(in));
	}

	public boolean isTagEmpty(String tag) {
		return compound.get(tag) == null;
	}

	public int getInt(String tag) {
		return compound.getInt(tag);
	}

	public float getFloat(String tag) {
		return compound.getFloat(tag);
	}
}
