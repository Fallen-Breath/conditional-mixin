package me.fallenbreath.conditionalmixin.api.mixin;

import me.fallenbreath.conditionalmixin.api.annotation.Restriction;

/**
 * A helper class to for checking if {@link Restriction} annotation on a mixin class is satisfied
 */
public interface RestrictionChecker
{
	/**
	 * Check if the {@link Restriction} annotation of the given mixin class is satisfied
	 * Return true if the mixin class doesn't have the {@link Restriction} annotation
	 * @param mixinClassName the target mixin class name to check dependency
	 */
	boolean checkRestriction(String mixinClassName);

	/**
	 * Set the callback function when the restriction check fails
	 */
	void setFailureCallback(RestrictionCheckFailureCallback failureCallback);
}
