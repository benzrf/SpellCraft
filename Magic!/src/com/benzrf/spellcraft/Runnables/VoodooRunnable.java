package com.benzrf.spellcraft.Runnables;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.benzrf.spellcraft.SpellCraft;
import com.benzrf.spellcraft.Spells;

public class VoodooRunnable extends SpellCraftRunnable
{
	public void run()
	{
		if (!lastPLoc.equals(p.getLocation()))
		{
			Location ll = p.getLocation().add(l);
			ll.setY(e.getLocation().getY());
			int id = ll.getBlock().getTypeId();
			if (id != 0 && id != 8 && id != 9 && id != 10 && id != 11)
			{
				e.teleport(ll.getWorld().getHighestBlockAt(ll).getLocation());
			}
			else
			{
				e.teleport(ll);
			}
			lastPLoc = p.getLocation();
			lastELoc = e.getLocation();
		}
		else
		{
			e.teleport(lastELoc);
			lastELoc = e.getLocation();
		}
		counter++;
		if (e.isDead())
		{
			counter = 151;
		}
		if (counter >= 150)
		{
			Spells.voodooMap.remove(p.getName());
			p.sendMessage(ChatColor.BLUE + "The voodoo fades...");
			SpellCraft.instance.getServer().getScheduler().cancelTask(id);
			Spells.rList.remove(this);
		}
	}
	
	@Override
	public String getSpellName()
	{
		return "voodoo";
	}
	
	@Override
	public boolean onStop()
	{
		return true;
	}
	
	@Override
	public Player getCaster()
	{
		return p;
	}
	
	private int counter = 0;
	public Location lastPLoc;
	public Location lastELoc;
	public int id = 0;
	public Location l;
	public boolean remove = true;
	public Entity e;
	public Player p;
}