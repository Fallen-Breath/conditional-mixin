package me.fallenbreath.conditionalmixin.api.mixin;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;

/**
 * A Tester class that will be constructed when parsing the {@link Condition} annotation
 * Your implemented class should have a public constructor with 0 argument
 */
public interface ConditionTester
{
	/**
	 * This method will be called when parsing the {@link Condition} annotation to determine if the condition is satisfied
	 * @param mixinClassName The class name of your mixin class. Passed from {@link org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin#shouldApplyMixin}
	 * @return a boolean indicating if the condition is satisfied
	 */
	boolean isSatisfied(String mixinClassName);
}
