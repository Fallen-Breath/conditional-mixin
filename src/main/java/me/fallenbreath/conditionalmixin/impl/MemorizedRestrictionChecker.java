package me.fallenbreath.conditionalmixin.impl;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * A restriction checker implementation which memorized the result
 */
public class MemorizedRestrictionChecker extends SimpleRestrictionChecker
{
	private final Map<String, Boolean> memory = Maps.newHashMap();

	public boolean checkRestriction(String mixinClassName)
	{
		return this.memory.computeIfAbsent(mixinClassName, key -> super.checkRestriction(mixinClassName));
	}
}
