package com.benzrf.spellcraft;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.benzrf.spellcraft.Runnables.TauntRunnable;

import uk.co.oliwali.HawkEye.util.HawkEyeAPI;

public class SpellCraftPlayerListener implements Listener
{
	public SpellCraftPlayerListener(SpellCraft plugin)
	{
		theplugin = plugin;
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		Player p = event.getPlayer();
		if (event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK))
		{
			return;
		}
		
		if (event.getItem() == null)
		{
			return;
		}
		Material h = event.getItem().getType();
		if (h.equals(Material.STICK) || h.equals(Material.BLAZE_ROD) || h.equals(Material.FEATHER))
		{
			{
				String target;
				String spellName;
				
				if (h.equals(Material.FEATHER))
				{
					if (!theplugin.playerMap4.containsKey(p.getName()))
					{
						return;
					}
					spellName = theplugin.playerMap4.get(p.getName());
					if (spellName == null || spellName.equals("none"))
					{
						return;
					}
					if (!(p.hasPermission(theplugin.permsName + ".spells." + spellName) || p.isOp() || p.hasPermission(theplugin.permsName + ".allspells")))
					{
						if (theplugin.permsLightning) p.getWorld().strikeLightning(p.getLocation());
						p.sendMessage(ChatColor.DARK_RED + theplugin.spellDeniedMessage.replaceAll("%spell", spellName).replaceAll("%SPELL", spellName.toUpperCase()));
						return;
					}
					target = "themself.";
				}
				else
				{
					if (!theplugin.playerMap.containsKey(p.getName()))
					{
						return;
					}
					spellName = theplugin.playerMap.get(p.getName());
					if (spellName == null || spellName.equals("none"))
					{
						return;
					}
					if (!(p.hasPermission(theplugin.permsName + ".spells." + spellName) || p.isOp() || p.hasPermission(theplugin.permsName + ".allspells")))
					{
						if (theplugin.permsLightning) p.getWorld().strikeLightning(p.getLocation());
						p.sendMessage(ChatColor.DARK_RED + theplugin.spellDeniedMessage.replaceAll("%spell", spellName).replaceAll("%SPELL", spellName.toUpperCase()));
						return;
					}
					if (theplugin.spellTypeMap.get(spellName) == ((byte) 1) && h.equals(Material.STICK))
					{
						return;
					}
					List<Block> list = event.getPlayer().getLineOfSight(null, 100);
					Location loc = list.get(list.size() - 1).getLocation();
					target = loc.toString();
				}
				
				if (!spellName.equals("snowball") && !checkManaAndDeduct(spellName, p))
				{
					return;
				}
				
				if (!spellName.equals("") && theplugin.HEEnabled)
				{
					HawkEyeAPI.addCustomEntry(theplugin, "Casted spell on " + target, p, p.getLocation(), spellName);
				}
				
				spellName = spellName.replace("/", "");
				if (spellName.equalsIgnoreCase("none"))
				{
					return;
				}
				try
				{
					boolean succeeded = (Boolean) Spells.class.getMethod(spellName, event.getClass()).invoke(null, event);
					if (!succeeded && p.getGameMode().equals(GameMode.SURVIVAL) && p.getGameMode().equals(GameMode.SURVIVAL))
					{
						refundMana(spellName, p, false);
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

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
	{
		Player p = event.getPlayer();
		if (p.getItemInHand() != null && p.getItemInHand().getType().equals(Material.WOOD_SWORD))
		{
			if (theplugin.playerMap2.containsKey(p.getName()))
			{
				String spellName = theplugin.playerMap2.get(p.getName());
				if (!(p.hasPermission(theplugin.permsName + ".spells." + spellName) || p.isOp() || p.hasPermission(theplugin.permsName + ".allspells")))
				{
					if (theplugin.permsLightning) p.getWorld().strikeLightning(p.getLocation());
					p.sendMessage(ChatColor.DARK_RED + theplugin.spellDeniedMessage.replaceAll("%spell", spellName).replaceAll("%SPELL", spellName.toUpperCase()));
					return;
				}
				if (!checkManaAndDeduct(spellName, p))
				{
					return;
				}
//				p.sendMessage(spellName);
				String target;
				if (event.getRightClicked() instanceof Player)
				{
					target = ((Player) event.getRightClicked()).getName();
				}
				else
				{
					target = "a(n) " + event.getRightClicked().getClass().getName();
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
						refundMana(spellName, p, false);
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

	@EventHandler
	public void onPlayerChat(final AsyncPlayerChatEvent event)
	{
		Player p = event.getPlayer();
		if (event.getMessage().equalsIgnoreCase(theplugin.sacrificePhrase) && (p.hasPermission(theplugin.permsName + ".sacrifice") || p.isOp()))
		{
			theplugin.getServer().getScheduler().scheduleSyncDelayedTask(theplugin, new Runnable() {
				public void run()
				{
					sacrifice(event);
				}				
			});
		}
		if (p.isOp() || p.hasPermission(theplugin.permsName + ".colorchat"))
		{
			String newMessage = event.getMessage();
			for (ChatColor c : ChatColor.values())
			{
				newMessage = newMessage.replaceAll(":" + c.name() + ":", c.toString());
			}
			newMessage = newMessage.replaceAll("%%%", "§");
			newMessage = newMessage.replaceAll("&&&", "§");
			event.setMessage(newMessage);
		}
	}
	
	public void sacrifice(AsyncPlayerChatEvent event)
	{
		Player p = event.getPlayer();
		String blocks = "";
		for (int x = -5; x <= -3; x++)
		{
			for (int y = -1; y <= 2; y++)
			{
				for (int z = -2; z <= 2; z++)
				{
					if (!(x == -4 && y == 0 && z == -1) && !(x == -4 && y == 0 && z == 0) && !(x == -4 && y == 0 && z == 1))
					{
						blocks = blocks + Integer.toString(p.getWorld().getBlockAt(p.getLocation().clone().add(x, y, z)).getTypeId()) + ", ";
					}
			 	}
			}
		}
		if (blocks.equals("1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 57, 41, 41, 41, 57, 0, 42, 42, 42, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, "))
		{
			ArrayList<String> sacrificeBlocks = new ArrayList<String>();
			if (p.getWorld().getBlockAt(p.getLocation().clone().add(-4, 0, 1)).getType() != Material.AIR)
			{
				sacrificeBlocks.add(p.getWorld().getBlockAt(p.getLocation().clone().add(-4, 0, 1)).getType().toString());
	 	 	}
			if (p.getWorld().getBlockAt(p.getLocation().clone().add(-4, 0, 0)).getType() != Material.AIR)
		 	{
				sacrificeBlocks.add(p.getWorld().getBlockAt(p.getLocation().clone().add(-4, 0, 0)).getType().toString());
	 	 	}
			if (p.getWorld().getBlockAt(p.getLocation().clone().add(-4, 0, -1)).getType() != Material.AIR)
			{
				sacrificeBlocks.add(p.getWorld().getBlockAt(p.getLocation().clone().add(-4, 0, -1)).getType().toString());
	 	 	}
			Collections.sort(sacrificeBlocks);
			if (theplugin.spellBlockMap.containsKey(sacrificeBlocks.toString()))
			{
				String spellName = theplugin.spellBlockMap.get(sacrificeBlocks.toString());
				if (!p.hasPermission(theplugin.permsName + ".spells." + spellName) && !p.hasPermission(theplugin.permsName + ".allspells"))
				{
					return;
				}
				if (!spellName.equals("") && theplugin.HEEnabled)
				{
					HawkEyeAPI.addCustomEntry(theplugin, "Sacrificed and gained spell", p, p.getLocation(), spellName);
				}
				event.setCancelled(true);
				theplugin.setSpell(new String[] {p.getName(), spellName});
				p.getWorld().strikeLightning(p.getLocation().getBlock().getLocation().clone().add(-4, 0, -1));
				p.getWorld().strikeLightning(p.getLocation().getBlock().getLocation().clone().add(-4, 0, 0));
				p.getWorld().strikeLightning(p.getLocation().getBlock().getLocation().clone().add(-4, 0, 1));
				p.getWorld().getBlockAt(p.getLocation().clone().add(-4, 0, -1)).setTypeId(0);
				p.getWorld().getBlockAt(p.getLocation().clone().add(-4, 0, 0)).setTypeId(0);
				p.getWorld().getBlockAt(p.getLocation().clone().add(-4, 0, 1)).setTypeId(0);
				p.sendMessage(theplugin.sacrificeMessage.replaceAll("%spell", spellName).replaceAll("%SPELL", spellName.toUpperCase()));
			}
		}
	}
	
	boolean checkManaAndDeduct(String spellName, Player p)
	{
		if (p.getGameMode().equals(GameMode.SURVIVAL))
		{
			int cost = theplugin.spellCostMap.get(spellName);
			int mana = theplugin.getMana(p);
			if (mana < cost)
			{
				p.sendMessage(ChatColor.RED + "Not enough mana!");
				return false;
			}
			theplugin.addMana(p, -cost);
			p.sendMessage(ChatColor.GOLD + Integer.toString(-cost) + ChatColor.GREEN + " Mana; " + ChatColor.GOLD + Integer.toString(mana - cost) + ChatColor.GREEN + " remaining.");
		}
		return true;
	}
	
	void refundMana(String spellName, Player p, boolean cause)
	{
		int cost = theplugin.spellCostMap.get(spellName);
		theplugin.addMana(p, cost);
		int mana = theplugin.getMana(p);
		if (cause)
		{
			p.sendMessage(ChatColor.GOLD + Integer.toString(cost) + ChatColor.GREEN + " Mana refunded; " + ChatColor.GOLD + Integer.toString(mana) + ChatColor.GREEN + " remaining.");
		}
		else
		{
			p.sendMessage(ChatColor.GREEN + "Spell failed! " + ChatColor.GOLD + Integer.toString(cost) + ChatColor.GREEN + " Mana refunded; " + ChatColor.GOLD + Integer.toString(mana) + ChatColor.GREEN + " remaining.");
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		if (Spells.tauntList.contains(event.getPlayer().getName()))
		{
			event.getItemDrop().setPickupDelay(500);
			Spells.tauntList.remove(event.getPlayer().getName());
			TauntRunnable t = new TauntRunnable();
			t.i = event.getItemDrop();
			t.p = event.getPlayer();
			t.id = theplugin.getServer().getScheduler().scheduleSyncRepeatingTask(theplugin, t, 60, 20);
			Spells.rList.add(t);
			event.getPlayer().sendMessage(ChatColor.BLUE + "Taunt expended!");
		}
	}
	SpellCraft theplugin;
}