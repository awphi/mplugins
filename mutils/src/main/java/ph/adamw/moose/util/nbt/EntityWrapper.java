package ph.adamw.moose.util.nbt;

import lombok.Getter;

import net.minecraft.server.v1_13_R2.EntityLiving;
import net.minecraft.server.v1_13_R2.NBTBase;
import net.minecraft.server.v1_13_R2.NBTTagByte;
import net.minecraft.server.v1_13_R2.NBTTagCompound;
import net.minecraft.server.v1_13_R2.NBTTagInt;
import net.minecraft.server.v1_13_R2.NBTTagString;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;

public class EntityWrapper<T extends Entity> {
	@Getter
	private T entity;

	private NBTTagCompound compound;

	@SuppressWarnings("unchecked")
	public EntityWrapper(T entity) {
		this.entity = entity;
		compound = new NBTTagCompound();
		asNMS().c(compound);
		this.entity = (T) asNMS().getBukkitEntity();
	}

	private net.minecraft.server.v1_13_R2.Entity  asNMS() {
		return ((CraftEntity) entity).getHandle();
	}

	private void writeToEntity() {
		((EntityLiving) asNMS()).a(compound);
	}

	private void setTag(String tag, NBTBase entry) {
		compound.set(tag, entry);
		writeToEntity();
	}

	public void setString(String tag, String entry) {
		setTag(tag, new NBTTagString(entry));
	}

	public void setInt(String tag, int entry) {
		setTag(tag, new NBTTagInt(entry));
	}

	public void setByte(String tag, byte entry) {
		setTag(tag, new NBTTagByte(entry));
	}

	public String getString(String tag) {
		return compound.getString(tag);
	}

	public int getInt(String tag) {
		return compound.getInt(tag);
	}
}
