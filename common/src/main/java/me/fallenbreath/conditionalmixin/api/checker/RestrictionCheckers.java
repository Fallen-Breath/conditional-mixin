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
	 * A memorized version of the given checker, that stores and reuses the check result for mixin class names
	 */
	public static RestrictionChecker memorized(RestrictionChecker checker)
	{
		return new MemorizedRestrictionChecker(checker);
	}

	/**
	 * The memorized checker that uses {@link #simple} as the internal checker implementation
	 * <br>
	 * See also: {@link #memorized(RestrictionChecker)}
	 */
	public static RestrictionChecker memorized()
	{
		return memorized(simple());
	}
}
