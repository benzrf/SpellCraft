package com.benzrf.spellcraft;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import uk.co.oliwali.HawkEye.util.HawkEyeAPI;

public class SpellCraftEntityListener implements Listener
{

	public SpellCraftEntityListener(SpellCraft plugin)
	{
		theplugin = plugin;
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event)
	{
		if (event.getEntity() instanceof Player)
		{ 
			return;
		}
		Player p;
		if (event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent)
		{
			if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof Player)
			{
				p = (Player) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();
				if ((p.hasPermission(theplugin.permsName + ".mana") || p.isOp()) && p.getGameMode().equals(GameMode.SURVIVAL))
				{
					p.sendMessage(ChatColor.GOLD + "+" + Integer.toString(event.getDroppedExp() * 10) + ChatColor.GREEN + " Mana");
					theplugin.addMana(p, event.getDroppedExp() * 10);
				}
			}
			if (((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager() instanceof Projectile)
			{
				if (((Projectile) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager()).getShooter() instanceof Player)
				{
					p = (Player) ((Projectile) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager()).getShooter();
					if ((p.hasPermission(theplugin.permsName + ".mana") || p.isOp()) && p.getGameMode().equals(GameMode.SURVIVAL))
					{
						p.sendMessage(ChatColor.GOLD + "+" + Integer.toString(event.getDroppedExp() * 10) + ChatColor.GREEN + " Mana");
						theplugin.addMana(p, event.getDroppedExp() * 10);
					}
				}
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent uncastedevent)
	{
		if (uncastedevent instanceof EntityDamageByEntityEvent)
		{
			EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) uncastedevent;
//			theplugin.thelogger.info("thingy2");
			if (event.getDamager() instanceof Snowball)
			{
				Snowball snowball = (Snowball) event.getDamager();
//				theplugin.thelogger.info("thingy");
				if (snowball.getShooter() instanceof Player)
				{
					Player p = (Player) snowball.getShooter();
					if (theplugin.playerMap3.containsKey(p.getName()))
					{
						String spellName = theplugin.playerMap3.get(p.getName());
						if (!(p.hasPermission(theplugin.permsName + ".spells." + spellName) || p.isOp() || p.hasPermission(theplugin.permsName + ".allspells")))
						{
							if (theplugin.permsLightning) p.getWorld().strikeLightning(p.getLocation());
							p.sendMessage(ChatColor.DARK_RED + theplugin.spellDeniedMessage.replaceAll("%spell", spellName).replaceAll("%SPELL", spellName.toUpperCase()));
							return;
						}
						if (!theplugin.thelistener.checkManaAndDeduct(spellName, p))
						{
							return;
						}
						String target;
						if (event.getEntity() instanceof Player)
						{
							target = p.getName();
						}
						else
						{
							target = "a(n) " + event.getEntity().getClass().getName();
						}
						if (!spellName.equals("") && theplugin.HEEnabled)
						{
							HawkEyeAPI.addCustomEntry(theplugin, "Casted spell on " + target, p, p.getLocation(), spellName);
						}
						if (spellName.equalsIgnoreCase("none"))
						{
							return;
						}
						try {
							boolean succeeded = (Boolean) Spells.class.getMethod(spellName, event.getClass()).invoke(null, event);
							if (!succeeded && p.getGameMode().equals(GameMode.SURVIVAL))
							{
								theplugin.thelistener.refundMana(spellName, p, false);
							}
						}
						catch (IllegalArgumentException e)
						{
							e.printStackTrace();
						}
						catch (SecurityException e)
						{
							e.printStackTrace();
						}
						catch (IllegalAccessException e)
						{
							e.printStackTrace();
						}
						catch (InvocationTargetException e)
						{
							e.printStackTrace();
						}
						catch (NoSuchMethodException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	private SpellCraft theplugin;
}
