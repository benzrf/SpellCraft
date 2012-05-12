package com.benzrf.spellcraft.Runnables;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import com.benzrf.spellcraft.Spells;

public class SummonwolfRunnable implements SpellCraftRunnable
{
	public void run()
	{
//		counter++;
//		if (counter >= 12)
//		{
//			counter = 0;
//			e.damage(1, p);
//			e.setHealth(e.getHealth() + 1);
//		}
		if (e.isDead())
		{
			w.getServer().getScheduler().cancelTask(id);
			Spells.rList.remove(this);
			w.getWorld().createExplosion(w.getLocation(), 0);
			w.remove();
		}
	}
	
	@Override
	public String getSpellName()
	{
		return "summonwolf";
	}
	
	@Override
	public boolean onStop()
	{
		w.getWorld().createExplosion(w.getLocation(), 0);
		w.remove();
		return true;
	}
	
	@Override
	public Player getCaster()
	{
		return p;
	}
	
	public Player p;
	public int id = 0;
	public LivingEntity e;
	public Wolf w;
//	int counter = 0;
}