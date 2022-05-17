package me.fallenbreath.conditionalmixin.api.mixin;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;

public interface ConditionTester
{
	/**
	 * This method will be called when parsing the {@link Condition} annotation to determine if the condition is satisfied
	 * @param mixinClassName The class name of your mixin class. Passed from
	 * @return a boolean indicating if the condition is satisfied
	 */
	boolean isSatisfied(String mixinClassName);
}
