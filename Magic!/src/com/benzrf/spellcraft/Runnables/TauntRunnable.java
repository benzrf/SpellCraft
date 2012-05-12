package com.benzrf.spellcraft.Runnables;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import com.benzrf.spellcraft.SpellCraft;
import com.benzrf.spellcraft.Spells;

public class TauntRunnable implements SpellCraftRunnable
{
	public void run()
	{
		i.setPickupDelay(500);
		if (i.getLocation().distance(p.getLocation()) < 2)
		{
			i.setPickupDelay(0);
			SpellCraft.instance.getServer().getScheduler().cancelTask(id);
			Spells.rList.remove(this);
		}
	}
	
	@Override
	public String getSpellName()
	{
		return "taunt";
	}
	
	@Override
	public boolean onStop()
	{
		i.setPickupDelay(0);
		return false;
	}
	
	@Override
	public Player getCaster()
	{
		return p;
	}
	
	public Player p;
	public Item i;
	public int id;
}
