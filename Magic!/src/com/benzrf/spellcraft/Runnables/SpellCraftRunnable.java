package com.benzrf.spellcraft.Runnables;

import org.bukkit.entity.Player;

public interface SpellCraftRunnable extends Runnable
{
	public void run();
	public String getSpellName();
	public boolean onStop();
	public Player getCaster();
}
