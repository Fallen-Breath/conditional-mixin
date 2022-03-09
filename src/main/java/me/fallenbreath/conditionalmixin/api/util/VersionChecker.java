package me.fallenbreath.conditionalmixin.api.util;

import me.fallenbreath.conditionalmixin.ConditionalMixinMod;
import net.fabricmc.loader.api.Version;

import java.lang.reflect.Method;
import java.util.Collection;

public class VersionChecker
{
	/**
	 * Use fabric-loader's util to check if a given version satisfy a version predicate
	 * @param version A Version object from fabric. You can use {@link Version#parse} to create one
	 * @param versionPredicate A string indicates a version predicate, e.g. ">=1.2.0" or "2.0.x"
	 */
	public static boolean doesVersionSatisfyPredicate(Version version, String versionPredicate)
	{
		try
		{
			// fabric loader >=0.12
			return net.fabricmc.loader.api.metadata.version.VersionPredicate.parse(versionPredicate).test(version);
		}
		catch (NoClassDefFoundError e)
		{
			// fabric loader >=0.10.4 <0.12
			try
			{
				Class<?> clazz = Class.forName("net.fabricmc.loader.util.version.VersionPredicateParser");
				Method matches = clazz.getMethod("matches", Version.class, String.class);
				return (boolean)matches.invoke(null, version, versionPredicate);
			}
			catch (Exception ex)
			{
				ConditionalMixinMod.LOGGER.error("Failed to invoke VersionPredicateParser#matches", ex);
			}
		}
		catch (Exception e)
		{
			ConditionalMixinMod.LOGGER.error("Failed to parse version or version predicate {} {}: {}", version.getFriendlyString(), versionPredicate, e);
		}
		return false;
	}

	/**
	 * The given version satisfies, if versionPredicates is empty, or any of the predicate satisfies
	 */
	public static boolean doesVersionSatisfyPredicate(Version version, Collection<String> versionPredicates)
	{
		return versionPredicates.isEmpty() || versionPredicates.stream().anyMatch(vp -> doesVersionSatisfyPredicate(version, vp));
	}
}
