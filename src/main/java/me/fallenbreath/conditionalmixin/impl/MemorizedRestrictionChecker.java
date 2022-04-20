package me.fallenbreath.conditionalmixin.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A restriction checker implementation which memorized the result
 */
public class MemorizedRestrictionChecker extends SimpleRestrictionChecker
{
	private final Map<String, Boolean> memory = new ConcurrentHashMap<>();

	public boolean checkRestriction(String mixinClassName)
	{
		return this.memory.computeIfAbsent(mixinClassName, key -> super.checkRestriction(mixinClassName));
	}
}
