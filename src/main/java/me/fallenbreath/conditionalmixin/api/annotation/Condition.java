package me.fallenbreath.conditionalmixin.api.annotation;

import me.fallenbreath.conditionalmixin.api.mixin.ConditionTester;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Condition
{
	Type type() default Type.MOD;

	/**
	 * A mod id if type == MOD, e.g. "lithium" or "carpet"
	 * A full qualified mixin class name if type == MIXIN
	 * Not used if type == TESTER
	 */
	String value() default "";

	/**
	 * All possible version range requirements
	 * The value is used when type == MOD
	 * The condition is satisfied when all the version matches any predicate or no predicate is given
	 * e.g. ">=1.2.3"
	 */
	String[] versionPredicates() default {};

	/**
	 * Specified your tester class implemented {@link ConditionTester}
	 * The value is used when type == TESTER
	 */
	Class<? extends ConditionTester> tester() default ConditionTester.class;

	enum Type
	{
		MOD,
		MIXIN,
		TESTER,
	}
}
