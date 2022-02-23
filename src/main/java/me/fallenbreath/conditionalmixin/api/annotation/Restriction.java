package me.fallenbreath.conditionalmixin.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Go apply this annotation on your mixin class
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Restriction
{
	/**
	 * Enable only when all given conditions are satisfied, like the "depends" entry in "fabric.mod.json"
	 */
	Condition[] require() default {};

	/**
	 * Disable if any given condition is satisfied, like the "conflicts" entry in "fabric.mod.json"
	 * Has higher priority than field "require"
	 */
	Condition[] conflict() default {};
}
