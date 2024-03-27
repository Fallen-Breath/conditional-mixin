package me.fallenbreath.conditionalmixin.api.annotation;

import me.fallenbreath.conditionalmixin.api.mixin.ConditionTester;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Condition
{
	/**
	 * Define when the type of this condition. See {@link Type} for more information
	 */
	Type type() default Type.MOD;

	/**
	 * <ul>
	 * <li>(type MOD): A mod id, e.g. "lithium" or "carpet"
	 * <li>(type MIXIN): A full qualified mixin class name, e.g. "com.example.mod.mixins.FooBarMixin",
	 * <li>(type TESTER): Unused</li>
	 * </ul>
	 */
	String value() default "";

	/**
	 * The value is used only when type == MOD
	 * <p>
	 * All possible version range requirements. The predicate syntax depends on the current mod loader platform
	 * <p>
	 * The condition is satisfied when the testing version matches any versionPredicate, or no versionPredicate is given
	 * <p>
	 * Examples (using fabric-like platforms syntax):
	 * <ul>
	 * <li>">=1.2.3": version >= 1.2.3</li>
	 * <li>">=1.2.3 <1.3": version in [1.2.3, 1.3) </li>
	 * <li>[">=1.2.3 <1.3", ">=2.0"]: version in [1.2.3, 1.3), or version >= 2.0</li>
	 * </ul>
	 * Examples (using forge-like platforms syntax):
	 * <ul>
	 * <li>"[1.2.3,)": version >= 1.2.3</li>
	 * <li>"[1.2.3, 1.3)": version in [1.2.3, 1.3) </li>
	 * <li>["[1.2.3, 1.3)", "[2.0,)"]: version in [1.2.3, 1.3), or version >= 2.0</li>
	 * </ul>
	 */
	String[] versionPredicates() default {};

	/**
	 * The value is used only when type == TESTER
	 * <p>
	 * Specified your tester class that implements {@link ConditionTester}
	 * <p>
	 * An object of the given class will be constructed to check the condition satisfaction
	 */
	Class<? extends ConditionTester> tester() default ConditionTester.class;

	enum Type
	{
		/**
		 * Satisfies when the given mod presents, and its version satisfied the given {@link #versionPredicates}
		 */
		MOD,

		/**
		 * Satisfies when the specified mixin class exists, and (if it has {@link Restriction}) its {@link Restriction} is satisfied
		 * <p>
		 * Notes: Please make sure the class name you provide in {@link #value} is a valid mixin class from your mod, or the behavior might be undefined
		 */
		MIXIN,

		/**
		 * Satisfies the given {@link #tester} object says ok
		 */
		TESTER,
	}
}
