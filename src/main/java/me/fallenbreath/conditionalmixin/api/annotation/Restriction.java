package me.fallenbreath.conditionalmixin.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The restrictions to be satisfied in order to apply the annotated mixin
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Restriction
{
	/**
	 * Enable only when all given conditions are satisfied, like the "depends" entry in "fabric.mod.json"
	 */
	Condition[] require() default {};

	/**
	 * Disable if any given condition is satisfied, like the "breaks" entry in "fabric.mod.json"
	 * Has higher priority than field "require"
	 */
	Condition[] conflict() default {};
}
