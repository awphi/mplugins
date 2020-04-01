package ph.adamw.moose.rpg.diet;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import ph.adamw.moose.rpg.state.RpgPlayer;

public class DietListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEat(PlayerItemConsumeEvent event) {
		event.setCancelled(true);

		final FoodData data = DietHandler.getFoodData(event.getItem().getType());
		if(data == null) {
			return;
		}

		event.getPlayer().getInventory().remove(event.getItem());
		RpgPlayer.from(event.getPlayer()).consume(FoodGroup.getFoodGroup(event.getItem().getType()), data.getValue(), data.getSaturation());
	}
}
