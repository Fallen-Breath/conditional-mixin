package me.fallenbreath.conditionalmixin.impl;

import com.google.common.collect.Maps;
import me.fallenbreath.conditionalmixin.api.checker.RestrictionChecker;
import me.fallenbreath.conditionalmixin.api.mixin.RestrictionCheckFailureCallback;

import java.util.Map;

/**
 * A restriction checker implementation which memorized the result
 */
public class MemorizedRestrictionChecker implements RestrictionChecker
{
	private final RestrictionChecker checker;
	private final Map<String, Boolean> memory = Maps.newHashMap();

	public MemorizedRestrictionChecker(RestrictionChecker checker)
	{
		this.checker = checker;
	}

	@Override
	public boolean checkRestriction(String mixinClassName)
	{
		synchronized (this.memory)
		{
			Boolean result = this.memory.get(mixinClassName);
			if (result == null)
			{
				result = this.checker.checkRestriction(mixinClassName);
				this.memory.put(mixinClassName, result);
			}
			return result;
		}
	}

	@Override
	public void setFailureCallback(RestrictionCheckFailureCallback failureCallback)
	{
		this.checker.setFailureCallback(failureCallback);
	}
}
