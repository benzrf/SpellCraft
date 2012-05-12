package com.benzrf.spellcraft.Runnables;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.benzrf.spellcraft.SpellCraft;
import com.benzrf.spellcraft.Spells;

public class ArrowshieldRunnable implements SpellCraftRunnable
{
	public void run()
	{
		counter--;
		if (counter <= 0)
		{
			SpellCraft.instance.getServer().getScheduler().cancelTask(id);
			Spells.rList.remove(this);
			p.sendMessage(ChatColor.BLUE + "The protection fades...");
		}
		ArrayList<Entity> eL = (ArrayList<Entity>) p.getNearbyEntities(5, 5, 5);
		for (Entity e : eL)
		{
			if (e instanceof Arrow)
			{
				e.setVelocity(new Vector(0, 0, 0));
			}
		}
	}
	
	@Override
	public String getSpellName()
	{
		return "arrowshield";
	}
	
	@Override
	public boolean onStop()
	{
		return false;
	}
	
	@Override
	public Player getCaster()
	{
		return p;
	}
	
	int counter = 500;
	public Player p;
	public int id;
}