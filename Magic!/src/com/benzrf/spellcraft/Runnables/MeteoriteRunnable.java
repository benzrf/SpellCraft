package com.benzrf.spellcraft.Runnables;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.bukkit.entity.Entity;

import com.benzrf.spellcraft.SpellCraft;
import com.benzrf.spellcraft.Spells;

public class MeteoriteRunnable implements SpellCraftRunnable
{
	public void run()
	{
		l.getBlock().setTypeId(0);
		l.getWorld().playEffect(l, Effect.MOBSPAWNER_FLAMES, 0, 20);
		l.getWorld().playEffect(l, Effect.SMOKE, 4, 20);
		l.setY(height);
		int t = l.getBlock().getTypeId();
		if (t == 0 || t == 8 || t == 9 || t == 10 || t == 11)
		{
			l.getBlock().setType(Material.OBSIDIAN);
		}
		else
		{
			Location l2;
			for (int x = -3; x <= 3; x++)
			{
				for (int y = -3; y <= 3; y++)
				{
					for (int z = -3; z <= 3; z++)
					{
						if (l.clone().add(x, y, z).distance(l) <= 4 && !l.clone().add(x, y, z).getBlock().getType().equals(Material.BEDROCK) && !l.clone().add(x, y, z).getBlock().getType().equals(Material.OBSIDIAN))
						{
							l2 = l.clone().add(x, y, z);
							l2.getBlock().setType(Material.FIRE);
							l2.getWorld().createExplosion(l2, 1);
							l2.getWorld().playEffect(l2, Effect.SMOKE, 4);
						}
					}
				}
			}
			Arrow a = l.getWorld().spawnArrow(l, new Vector(0, 0, 0), 0, 0);
			for (Entity e : a.getNearbyEntities(4, 4, 4))
			{
				if (e instanceof LivingEntity)
				{
					((LivingEntity) e).damage(100);
				}
				else if (!(e instanceof EnderCrystal) && !(e instanceof EnderDragon))
				{
					e.remove();
				}
			}
			a.remove();
			SpellCraft.instance.getServer().getScheduler().cancelTask(id);
			Spells.rList.remove(this);
		}
		height--;
	}
	
	@Override
	public String getSpellName()
	{
		return "meteorite";
	}
	
	@Override
	public boolean onStop()
	{
		l.getBlock().setTypeId(0);
		return true;
	}
	
	@Override
	public Player getCaster()
	{
		return p;
	}
	
	public Player p;
	public Location l;
	public int height = 255;
	public int id = 0;
}
