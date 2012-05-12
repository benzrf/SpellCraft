package com.benzrf.spellcraft.Runnables;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.benzrf.spellcraft.SpellCraft;
import com.benzrf.spellcraft.Spells;

public class HeisalreadyhereRunnable implements SpellCraftRunnable
{
	public void run()
	{
		if (status)
		{
			p.chat("§eW§9H§4E§dN§f §cI§f §2A§4M§f §eA§0L§9R§4E§dA§cD§eY§f §4H§0E§eR§9E§4?");
			p.getWorld().playEffect(p.getLocation(), Effect.POTION_BREAK, 228);
			p.teleport(t);
			SpellCraft.instance.getServer().getScheduler().cancelTask(id);
			Spells.rList.remove(this);
			return;
		}
		if (t.getLocation().distance(p.getLocation()) > 48)
		{
			p.chat(ChatColor.DARK_GREEN + "HOW DO YOU EXPECT TO OUTRUN ME...");
			status = true;
		}
	}
	
	Location getLaunchPoint(Player p, int multiplier)
	{
		Location fromloc = p.getEyeLocation();
		float adjuster = p.getLocation().getPitch();
		if (adjuster < 45)
		{
			adjuster = adjuster / 30;
		}
		else
		{
			adjuster = 1;
		}
		fromloc = fromloc.add(fromloc.getDirection().multiply(adjuster * multiplier));
		return fromloc;
	}
	
	@Override
	public String getSpellName()
	{
		return "heisalreadyhere";
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
	public Player t;
	public int id;
	public boolean status = false;
}
