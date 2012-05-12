package com.benzrf.spellcraft.Runnables;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.benzrf.spellcraft.Spells;

public class FissureRunnable implements SpellCraftRunnable
{
	public void run()
	{
		loc.getWorld().playEffect(loc, Effect.BLAZE_SHOOT, 1);
		loc.getWorld().playEffect(loc, Effect.ENDER_SIGNAL, 1);
		int loopTo = loc.getBlockY();
		int currentIndex = 0;
		for (int y = 0; y <= loopTo; y++)
		{
			loc.setY(y);
			currentIndex = blocks.size() - y - 1;
			if (blocks.get(currentIndex) != 257)
			{
				loc.getBlock().setTypeIdAndData(blocks.get(currentIndex), blocks2.get(currentIndex), false);
			}
		}
		Spells.subfissureCount++;
	}
	
	@Override
	public String getSpellName()
	{
		return "fissure";
	}
	
	@Override
	public boolean onStop()
	{
		run();
		return false;
	}
	
	@Override
	public Player getCaster()
	{
		return p;
	}
	
	public Player p;
	public Logger thelogger;
	public Location loc;
	public ArrayList<Integer> blocks;
	public ArrayList<Byte> blocks2;
}
