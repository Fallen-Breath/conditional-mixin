package me.fallenbreath.conditionalmixin.impl;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * A restriction checker implementation which memorized the result
 */
public class MemorizedRestrictionChecker extends SimpleRestrictionChecker
{
	private final Map<String, Boolean> memory = Maps.newHashMap();

	public synchronized boolean checkRestriction(String mixinClassName)
	{
		Boolean result = this.memory.get(mixinClassName);
		if (result == null)
		{
			result = super.checkRestriction(mixinClassName);
			this.memory.put(mixinClassName, result);
		}
		return result;
	}
}
