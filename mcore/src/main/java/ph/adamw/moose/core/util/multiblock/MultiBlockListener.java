package ph.adamw.moose.core.util.multiblock;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import ph.adamw.moose.core.MCore;
import ph.adamw.moose.core.util.chat.ChatUtils;

import java.util.Set;

public class MultiBlockListener implements Listener {
	public MultiBlockListener() {
		MCore.getPlugin().getServer().getPluginManager().registerEvents(this, MCore.getPlugin());
	}

	private MultiBlock getMultiBlockFromElement(Block block) {
		final String blockLocation = block.getLocation().toString();

		if(MultiBlockHandler.getProtectionSection().contains(blockLocation)) {
			final String coreLocation = (String) MultiBlockHandler.getProtectionSection().get(blockLocation);

			if (MultiBlockHandler.getMultiblocksSection().contains(coreLocation)) {
				return (MultiBlock) MultiBlockHandler.getMultiblocksSection().get(coreLocation);
			}
		}

		return null;
	}

	private MultiBlock getMultiBlockFromCore(Block block) {
		final String str = block.getLocation().toString();

		if(MultiBlockHandler.getMultiblocksSection().contains(str)) {
			return (MultiBlock) MultiBlockHandler.getMultiblocksSection().get(str);
		}

		return null;
	}

	private MultiBlock getMultiBlock(Block block) {
		final MultiBlock x = getMultiBlockFromElement(block);
		return x == null ? getMultiBlockFromCore(block) : x;
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if(event.getPlayer().isSneaking() || !event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || !event.getHand().equals(EquipmentSlot.HAND)) {
			return;
		}

		final MultiBlock mb = getMultiBlockFromCore(event.getClickedBlock());

		if(mb == null) {
			for(MultiBlock i : MultiBlockHandler.getMultiBlocks()) {
				if(i.getPattern().isValidNewStructure(event.getClickedBlock().getLocation())) {
					final MultiBlock built = MultiBlockHandler.buildAt(event.getPlayer(), i, event.getClickedBlock().getLocation());

					if(built != null) {
						ChatUtils.messageInfo(event.getPlayer(), "Structure Built!", "Created a new {" + built.getName() + "}.");
						event.setCancelled(true);
						break;
					}
				}
			}
		} else if(event.getPlayer().equals(mb.getOwner()) || mb.isTrusted(event.getPlayer())) {
			mb.onActivate(event);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent event) {
		final MultiBlock mb = getMultiBlock(event.getBlock());

		if(mb == null) {
			return;
		}

		if(!event.getPlayer().equals(mb.getOwner())) {
			ChatUtils.messageError(event.getPlayer(), "Invalid Permissions!", "You must be the owner of this {" + mb.getName() + "} to do that.");
			event.setCancelled(true);
			return;
		}

		ChatUtils.messageInfo(event.getPlayer(), "Structure Broken!", "Destroyed your {" + mb.getName() + "}.");
		mb.destroy();
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		// Deals with overriding blocks like placing a block on fire or water etc.
		final MultiBlock mb = getMultiBlock(event.getBlock());

		if(mb != null) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockFadeEvent(BlockFadeEvent event) {
		final MultiBlock mb = getMultiBlock(event.getBlock());
		if (mb != null) {

			if(mb.getOwner().isOnline()) {
				ChatUtils.messageInfo(mb.getOwner().getPlayer(), "Structure Broken!", "The {" + event.getBlock().getType().name().toLowerCase() + "} block of your {" + mb.getName() + "} faded.");
			}

			mb.destroy();
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		// Divert fire destruction to the breakblock listener to handler destruction of multiblocks containing fire.
		final Block block = event.getPlayer().getTargetBlock(null, 5);

		if (event.getAction().equals(Action.LEFT_CLICK_BLOCK) && block.getType().equals(Material.FIRE)) {
			onBreak(new BlockBreakEvent(block, event.getPlayer()));
		}
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
