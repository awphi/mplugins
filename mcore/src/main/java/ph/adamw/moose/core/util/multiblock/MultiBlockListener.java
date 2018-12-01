package ph.adamw.moose.core.util.multiblock;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import ph.adamw.moose.core.MCore;
import ph.adamw.moose.core.util.chat.ChatUtils;

public class MultiBlockListener implements Listener {
	private final MultiBlockHandler handler;

	public MultiBlockListener(MultiBlockHandler handler) {
		this.handler = handler;
		MCore.getPlugin().getServer().getPluginManager().registerEvents(this, MCore.getPlugin());
	}

	private MultiBlock getMultiBlockFromElement(Block block) {
		final String blockLocation = block.getLocation().toString();

		if(handler.getProtectionSection().contains(blockLocation)) {
			final String coreLocation = (String) handler.getProtectionSection().get(blockLocation);

			if (handler.getMultiblocksSection().contains(coreLocation)) {
				return (MultiBlock) handler.getMultiblocksSection().get(coreLocation);
			}
		}

		return null;
	}

	private MultiBlock getMultiBlockFromCore(Block block) {
		if(handler.getMultiblocksSection().contains(block.getLocation().toString())) {
			return (MultiBlock) handler.getMultiblocksSection().get(block.getLocation().toString());
		}

		return null;
	}

	private MultiBlock getMultiBlock(Block block) {
		final MultiBlock x = getMultiBlockFromElement(block);
		return x == null ? getMultiBlockFromCore(block) : x;
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if(!(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getHand().equals(EquipmentSlot.OFF_HAND)) || event.getPlayer().isSneaking()) {
			return;
		}

		final MultiBlock mb = getMultiBlockFromCore(event.getClickedBlock());

		if(mb == null) {
			for(MultiBlock i : handler.getMultiBlocks()) {
				if(i.getPattern().isValidNewStructure(event.getClickedBlock().getLocation(), event.getClickedBlock().getType())) {
					final MultiBlock built = MCore.getPlugin().getMultiBlockHandler().buildAt(event.getPlayer(), i, event.getClickedBlock().getLocation());

					if(built != null) {
						ChatUtils.messageInfo(event.getPlayer(), "Structure Built!", "Created a new {" + built.getName() + "}.");
						built.onCreate(event.getPlayer());
						event.setCancelled(true);
						break;
					}
				}
			}
		} else if(event.getPlayer().getUniqueId().equals(mb.getOwner()) || mb.isTrusted(event.getPlayer())) {
			mb.onActivate(event.getPlayer());
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		final MultiBlock mb = getMultiBlock(event.getBlock());

		if(mb == null) {
			return;
		}

		if(!event.getPlayer().getUniqueId().equals(mb.getOwner())) {
			ChatUtils.messageError(event.getPlayer(), "Invalid Permissions!", "You must be the owner of this {" + mb.getName() + "} to do that.");
			event.setCancelled(true);
			return;
		}

		mb.onDestroy(event.getPlayer());
		ChatUtils.messageInfo(event.getPlayer(), "Structure Broken!", "Destroyed your {" + mb.getName() + "}.");

		// Prune the configs of this multiblock
		for(Location i : mb.getPattern().traversePattern(mb.getCoreLocation())) {
			handler.getProtectionSection().set(i.toString(), null);
		}

		handler.getMultiblocksSection().set(mb.getCoreLocation().toString(), null);
	}

	@EventHandler
	public void onEndermanPickup(EntityChangeBlockEvent event) {
		if(getMultiBlock(event.getBlock()) != null) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockExplosion(BlockExplodeEvent event) {
		for(Block i : event.blockList()) {
			if(getMultiBlock(i) != null) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void onEntityExplosion(EntityExplodeEvent event) {
		for(Block i : event.blockList()) {
			if(getMultiBlock(i) != null) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void onPistonPull(BlockPistonRetractEvent event) {
		for(Block i : event.getBlocks()) {
			if(getMultiBlock(i) != null) {
				event.setCancelled(true);
				return;
			}
		}
	}
}
