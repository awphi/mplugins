package ph.adamw.moose.rpg.brewing.effect;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import ph.adamw.moose.rpg.MRpg;

public class BrewEffectIgnition extends BrewEffect implements Listener {
	public BrewEffectIgnition() {
		super("IGNITION");
		MRpg.getPlugin().getServer().getPluginManager().registerEvents(this, MRpg.getPlugin());
	}

	@Override
	public void applyExtraEffects(Player player, int potency, int length) {}

	@EventHandler
	public void onHit(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Player) {
			final Player player = (Player) event.getDamager();

			if(!BrewEffect.hasEffect(player, this, 0)) {
				return;
			}

			event.getEntity().setFireTicks(200);
		}
	}
}
