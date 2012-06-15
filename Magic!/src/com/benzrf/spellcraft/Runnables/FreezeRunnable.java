package com.benzrf.spellcraft.Runnables;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.benzrf.spellcraft.SpellCraft;
import com.benzrf.spellcraft.Spells;

public class FreezeRunnable extends SpellCraftRunnable
{
	public void run()
	{
		if (!l.getBlock().getLocation().equals(e.getLocation().getBlock().getLocation()))
		{
			e.teleport(l);
		}
		counter--;
		if (counter <= 0)
		{
			SpellCraft.instance.getServer().getScheduler().cancelTask(id);
			Spells.rList.remove(this);
			p.sendMessage(ChatColor.BLUE + "They've thawed out!");
		}
	}
	
	@Override
	public String getSpellName()
	{
		return "freeze";
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
	
	public Entity e;
	public Location l;
	public int id = 0;
	public int counter = 20;
	public Player p;
}
