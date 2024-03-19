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

	public static Optional<String> getVersionString(String modId)
	{
		return FabricLoader.getInstance().getModContainer(modId).map(c -> c.getMetadata().getVersion().getFriendlyString());
	}

	public static boolean doesVersionSatisfyPredicate(String modId, String versionPredicate)
	{
		Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(modId);
		if (!modContainer.isPresent()) return false;
		Version version = modContainer.get().getMetadata().getVersion();
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
}
