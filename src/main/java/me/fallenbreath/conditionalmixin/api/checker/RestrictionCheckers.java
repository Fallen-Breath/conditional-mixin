package me.fallenbreath.conditionalmixin.api.checker;

import me.fallenbreath.conditionalmixin.impl.MemorizedRestrictionChecker;
import me.fallenbreath.conditionalmixin.impl.SimpleRestrictionChecker;

/**
 * Factory for {@link RestrictionChecker}
 */
public class RestrictionCheckers
{
	/**
	 * A basic implementation of a {@link RestrictionChecker}
	 */
	public static RestrictionChecker simple()
	{
		return new SimpleRestrictionChecker();
	}

	/**
	 * A wrapped version of {@link #simple}, that stores and reuses the check result by mixin class names
	 */
	public static RestrictionChecker memorized()
	{
		return new MemorizedRestrictionChecker();
	}
}
