package me.fallenbreath.conditionalmixin.api.util;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.util.Collection;
import java.util.Optional;

@SuppressWarnings("ConstantValue")
public class VersionChecker
{
	/**
	 * Return if the given mod presents in the current environment
	 *
	 * @param modId The id of the mod to check
	 */
	@ExpectPlatform
	public static boolean isModPresent(String modId)
	{
		throw new AssertionError();
	}

	/**
	 * Return a string version of the given mod, or {@link Optional#empty} if the mod doesn't exist
	 *
	 * @param modId The id of the mod to get version from
	 */
	@ExpectPlatform
	public static Optional<String> getModVersionString(String modId)
	{
		throw new AssertionError();
	}

	/**
	 * Use the loader's util to check if a given version satisfy a version predicate
	 *
	 * @param modId            The id of the mod to test the given version predicate
	 * @param versionPredicate A string indicates a version predicate, in the syntax of the current platform. For example:
	 *                         <ul>
	 *                           <li>">=1.2.0" or "2.0.x" for fabric-like platforms</li>
	 *                           <li>"[1.2.0,)" or "[2.0, 2.1)" for forge-like platforms</li>
	 *                         </ul>
	 */
	@ExpectPlatform
	public static boolean doesModVersionSatisfyPredicate(String modId, String versionPredicate)
	{
		throw new AssertionError();
	}

	/**
	 * If the given mod version satisfies any of the predicate, or given versionPredicates is empty
	 * <p>
	 * If the given mod does not exist, false will be returned
	 *
	 * @param modId             The id of the mod to test the given version predicates
	 * @param versionPredicates A collection of the version predicates to test
	 */
	public static boolean doesModVersionSatisfyPredicate(String modId, Collection<String> versionPredicates)
	{
		if (!isModPresent(modId))
		{
			return false;
		}
		return versionPredicates.isEmpty() || versionPredicates.stream().anyMatch(vp -> doesModVersionSatisfyPredicate(modId, vp));
	}

	// ================================= Deprecated =================================

	/**
	 * Notes: Only works in fabric, just for backward-compatibility
	 *
	 * @param version          Should be a {@link net.fabricmc.loader.api.Version} instance, the version to check
	 * @param versionPredicate The fabric-syntax version predicate string, e.g. ">=1.2.0" or "2.0.x"
	 * @deprecated Use {@link #doesModVersionSatisfyPredicate} instead, which is platform-independent
	 */
	@ExpectPlatform
	@Deprecated
	public static boolean doesVersionSatisfyPredicate(Object version, String versionPredicate)
	{
		throw new AssertionError();
	}

	/**
	 * Notes: Only works in fabric, just for backward-compatibility
	 *
	 * @param version           Should be a {@link net.fabricmc.loader.api.Version} instance, the version to check
	 * @param versionPredicates A collection of the version predicates to test
	 * @deprecated Use {@link #doesModVersionSatisfyPredicate} instead, which is platform-independent
	 */
	@Deprecated
	public static boolean doesVersionSatisfyPredicate(Object version, Collection<String> versionPredicates)
	{
		return versionPredicates.isEmpty() || versionPredicates.stream().anyMatch(vp -> doesVersionSatisfyPredicate(version, vp));
	}
}
