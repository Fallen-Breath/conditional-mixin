package me.fallenbreath.conditionalmixin.api.util.fabric;

import me.fallenbreath.conditionalmixin.ConditionalMixinMod;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;

import java.lang.reflect.Method;
import java.util.Optional;

public class VersionCheckerImpl
{
	public static boolean isModPresent(String modId)
	{
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	public static Optional<String> getModVersionString(String modId)
	{
		return FabricLoader.getInstance().getModContainer(modId).map(c -> c.getMetadata().getVersion().getFriendlyString());
	}

	private static boolean doesVersionSatisfyPredicateImpl(Version version, String versionPredicate)
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

	@Deprecated
	public static boolean doesVersionSatisfyPredicate(Object version, String versionPredicate)
	{
		return doesVersionSatisfyPredicateImpl((Version)version, versionPredicate);
	}

	public static boolean doesModVersionSatisfyPredicate(String modId, String versionPredicate)
	{
		Optional<ModContainer> mod = FabricLoader.getInstance().getModContainer(modId);
		return mod.isPresent() && doesVersionSatisfyPredicateImpl(mod.get().getMetadata().getVersion(), versionPredicate);
	}
}
