package me.fallenbreath.conditionalmixin.util;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Collection;

public class FabricUtil
{
	public static final Logger LOGGER = LogManager.getLogger();

	public static boolean isModLoaded(String modId)
	{
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	public static boolean doesVersionFitsPredicate(Version version, String versionPredicate)
	{
		try
		{
			// fabric loader >=0.12
			return VersionPredicate.parse(versionPredicate).test(version);
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
				LOGGER.error("Failed to invoke VersionPredicateParser#matches", ex);
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to parse version or version predicate {} {}: {}", version.getFriendlyString(), versionPredicate, e);
		}
		return false;
	}

	/**
	 * Satisfy when the versionPredicates is empty or any of the predicate satisfies
	 */
	public static boolean doesVersionFitsPredicate(Version version, Collection<String> versionPredicates)
	{
		return versionPredicates.isEmpty() || versionPredicates.stream().anyMatch(vp -> doesVersionFitsPredicate(version, vp));
	}

	public static boolean doesModFitsAnyPredicate(String modId, Collection<String> versionPredicates)
	{
		return FabricLoader.getInstance().getModContainer(modId).
				map(mod -> doesVersionFitsPredicate(mod.getMetadata().getVersion(), versionPredicates)).
				orElse(false);
	}
}
