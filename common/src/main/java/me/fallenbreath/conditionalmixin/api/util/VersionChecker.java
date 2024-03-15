package me.fallenbreath.conditionalmixin.api.util;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.util.Collection;

public class VersionChecker
{
	@ExpectPlatform
	public static boolean isModPresent(String modId)
	{
		throw new AssertionError();
	}

	@ExpectPlatform
	public static String getVersionString(String modId)
	{
		throw new AssertionError();
	}

	/**
	 * Use the loader's util to check if a given version satisfy a version predicate
	 * @param modId The id of the mod to test the version of
	 * @param versionPredicate A string indicates a version predicate, e.g. ">=1.2.0" or "2.0.x"
	 */
	@ExpectPlatform
	public static boolean doesVersionSatisfyPredicate(String modId, String versionPredicate)
	{
		throw new AssertionError();
	}

	/**
	 * The given mod satisfies, if versionPredicates is empty, or any of the predicate satisfies
	 */
	public static boolean doesVersionSatisfyPredicate(String modId, Collection<String> versionPredicates)
	{
		return versionPredicates.isEmpty() || versionPredicates.stream().anyMatch(vp -> doesVersionSatisfyPredicate(modId, vp));
	}
}
