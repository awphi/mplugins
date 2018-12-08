package ph.adamw.moose.survival.region;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import ph.adamw.moose.core.MCore;
import ph.adamw.moose.core.util.chat.ChatUtils;
import ph.adamw.moose.survival.MSurvival;

public class RegionListener implements Listener {
	private final RegionHandler handler;

	public RegionListener(RegionHandler handler) {
		this.handler = handler;
		MSurvival.getPlugin().getServer().getPluginManager().registerEvents(this, MSurvival.getPlugin());
	}

	private void blockEvent(Cancellable event, Location location, Player player, RegionRank rank) {
		final Region region = handler.getRegion(location);

		if(region == null) {
			return;
		}

		if(!region.containsPlayer(player) || !region.getRankOf(player).canPerform(rank)) {
			event.setCancelled(true);
			ChatUtils.messageError(player, "No Permissions!", "You don't have permission to do that here.");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent event) {
		if(!event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.STICK)) {
			return;
		}

		if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			event.setCancelled(true);
			handler.defineCorner(true, event.getPlayer(), event.getClickedBlock().getLocation());
		} else if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getHand().equals(EquipmentSlot.OFF_HAND)) {
			handler.defineCorner(false, event.getPlayer(), event.getClickedBlock().getLocation());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		blockEvent(event, event.getBlock().getLocation(), event.getPlayer(), RegionRank.BUILDER);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		blockEvent(event, event.getBlock().getLocation(), event.getPlayer(), RegionRank.BUILDER);
	}

	@EventHandler
	public void onEntityClick(PlayerInteractEntityEvent event) {
		blockEvent(event, event.getRightClicked().getLocation(), event.getPlayer(), RegionRank.VISITOR);
	}
}
