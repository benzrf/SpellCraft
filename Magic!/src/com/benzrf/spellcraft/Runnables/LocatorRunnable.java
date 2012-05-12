package com.benzrf.spellcraft.Runnables;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class LocatorRunnable implements SpellCraftRunnable
{
	public void run()
	{
		Player p = event.getPlayer();
		if (p.getItemInHand() == null)
		{
			p.sendMessage(ChatColor.RED + "That item doesn't correspond to any ore!");
			return;
		}
		Material id = p.getItemInHand().getType();
		if (id.equals(Material.COAL))
		{
			id = Material.COAL_ORE;
		}
		else if (id.equals(Material.REDSTONE))
		{
			id = Material.REDSTONE_ORE;
		}
		else if (id.equals(Material.IRON_INGOT))
		{
			id = Material.IRON_ORE;
		}
		else if (id.equals(Material.GOLD_INGOT))
		{
			id = Material.GOLD_ORE;
		}
		else if (id.equals(Material.DIAMOND))
		{
			id = Material.DIAMOND_ORE;
		}
		else if (id.equals(Material.IRON_ORE) || id.equals(Material.GOLD_ORE))
		{
		}
		else
		{
			p.sendMessage(ChatColor.RED + "That item doesn't correspond to any ore!");
			return;
		}
		int px = p.getLocation().getBlockX();
		int py = p.getLocation().getBlockY();
		int pz = p.getLocation().getBlockZ();
		for (int y = 127; y >= 0; y--)
		{
			for (int x = px - 15; x <= px + 15; x++)
			{
				for (int z = pz - 15; z <= pz + 15; z++)
				{
					if (id.equals(p.getWorld().getBlockAt(x, y, z).getType()))
					{
						p.teleport(new Location(p.getWorld(), x, py, z));
						if (py > 65)
						{
							p.teleport(p.getWorld().getHighestBlockAt(x, z).getLocation().add(0.5, 0, 0.5));
						}
						else
						{
							p.teleport(new Location(p.getWorld(), x, py, z).add(0.5, 0, 0.5));
							int typeId = p.getLocation().getBlock().getTypeId();
							if ((typeId > 0 && typeId < 6) || (typeId == 7) || (typeId > 11 && typeId < 27) || (typeId == 29) || (typeId == 33) ||(typeId == 35) || (typeId == 36) || (typeId > 40 && typeId < 50) || (typeId > 51 && typeId < 55) || (typeId > 55 && typeId < 59) || (typeId > 59 && typeId < 65) || (typeId == 67) || (typeId == 71) || (typeId == 73) || (typeId == 74) || (typeId > 77 && typeId < 83) || (typeId > 83 && typeId < 90) || (typeId == 91) || (typeId == 92) || (typeId == 95))
							{
								p.getLocation().getBlock().setTypeId(0);
							}
							typeId = p.getLocation().clone().add(0, 1, 0).getBlock().getTypeId();
							if ((typeId > 0 && typeId < 6) || (typeId == 7) || (typeId > 11 && typeId < 27) || (typeId == 29) || (typeId == 33) ||(typeId == 35) || (typeId == 36) || (typeId > 40 && typeId < 50) || (typeId > 51 && typeId < 55) || (typeId > 55 && typeId < 59) || (typeId > 59 && typeId < 65) || (typeId == 67) || (typeId == 71) || (typeId == 73) || (typeId == 74) || (typeId > 77 && typeId < 83) || (typeId > 83 && typeId < 90) || (typeId == 91) || (typeId == 92) || (typeId == 95))
							{
								p.getLocation().clone().add(0, 1, 0).getBlock().setTypeId(0);
							}
						}
						p.sendMessage(ChatColor.BLUE + "Ore located!");
						return;
					}
				}
			}
		}
		p.sendMessage(ChatColor.RED + "None found.");
	}
	
	@Override
	public String getSpellName()
	{
		return "locator";
	}

	@Override
	public boolean onStop()
	{
		return true;
	}
	
	@Override
	public Player getCaster()
	{
		return event.getPlayer();
	}
	
	public PlayerInteractEvent event;
}
