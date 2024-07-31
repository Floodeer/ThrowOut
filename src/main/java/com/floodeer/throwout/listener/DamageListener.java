package com.floodeer.throwout.listener;

import com.floodeer.throwout.ThrowOut;
import com.floodeer.throwout.game.Game;
import com.floodeer.throwout.game.GamePlayer;
import com.floodeer.throwout.util.MathUtils;
import com.floodeer.throwout.util.VelocityUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;

public class DamageListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageByEntityEvent e) {
        if(!(e.getDamager() instanceof Player) && (!(e.getEntity() instanceof Player)))
            return;

        Player damager = (Player)e.getDamager();
        Player target = (Player)e.getEntity();
        if(GamePlayer.get(damager) == null || GamePlayer.get(target) == null)
            return;
        if(GamePlayer.get(damager).isInGame() && GamePlayer.get(target).isInGame()) {
            e.setCancelled(true);
            if(GamePlayer.get(damager).getGame().getState() != Game.GameState.IN_GAME)
                return;

            GamePlayer gpDamager = GamePlayer.get(damager);
            GamePlayer gpTarget = GamePlayer.get(target);

            if(gpTarget.getKnockbackPercentage() < 100) {
                if(gpDamager.isHasMegaPunch())
                    gpTarget.setKnockbackPercentage(gpTarget.getKnockbackPercentage() + MathUtils.random(ThrowOut.get().getConfigOptions().megaPunchPercentageMin, ThrowOut.get().getConfigOptions().megaPunchPercentageMax));
                else
                    gpTarget.setKnockbackPercentage(gpTarget.getKnockbackPercentage() + MathUtils.random(ThrowOut.get().getConfigOptions().hitPercentageMin, ThrowOut.get().getConfigOptions().hitPercentageMax));
            }
            target.damage(0);

            if(gpDamager.getKnockbackPercentage() >= 200) {
                VelocityUtils.velocity(target, VelocityUtils.getTrajectory2d(damager.getLocation(), target.getLocation()), 100, false, 0, 0, 0.4 + 0, false);
                return;
            }

            //WIP
            Location origin = damager.getLocation();

            double knockback = e.getDamage();
            if(knockback < 2)
                knockback = 2;

            knockback = Math.log10(knockback);

            Vector trajectory = VelocityUtils.getTrajectory2d(origin, target.getLocation());
            trajectory.multiply(0.6 * knockback);
            trajectory.setY(Math.abs(trajectory.getY()));

            double vel = 0.2 + trajectory.length() * 0.8;
            double modifier = MathUtils.toDecimal(gpTarget.getKnockbackPercentage());

            if(modifier < 0.1)
                vel = 0;

            VelocityUtils.velocity(target, trajectory, (modifier < 0.1) ? 0 : vel * modifier, false, 0,
                    Math.abs(0.2 * knockback),
                    0.4 + (0.04 * knockback),
                    true);
        }
    }
}
