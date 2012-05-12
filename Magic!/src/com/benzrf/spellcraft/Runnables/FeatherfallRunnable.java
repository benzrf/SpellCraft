package com.benzrf.spellcraft.Runnables;

import org.bukkit.entity.Player;

import com.benzrf.spellcraft.SpellCraft;
import com.benzrf.spellcraft.Spells;

public class FeatherfallRunnable implements SpellCraftRunnable
{
	public void run()
	{
		if (p.getLocation().subtract(0, 1, 0).getBlock().getTypeId() != 0)
		{
			m.getServer().getScheduler().cancelTask(id);
			Spells.rList.remove(this);
			return;
		}
		p.setFallDistance(0);
	}
	
	@Override
	public String getSpellName()
	{
		return "superjump/featherfall";
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
	
	public Player p;
	public int id = 0;
	public SpellCraft m;
}
