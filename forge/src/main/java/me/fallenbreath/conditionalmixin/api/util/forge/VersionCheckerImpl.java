package me.fallenbreath.conditionalmixin.api.util.forge;

import me.fallenbreath.conditionalmixin.ConditionalMixinMod;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Optional;

public class VersionCheckerImpl
{
	/**
	 * {@link net.minecraftforge.fml.loading.LoadingModList} does not exist until forge
	 */
	private static Optional<IModFileInfo> getLoadingModFileInfo(String modId)
	{
		try
		{
			// Forge 26.1+ changed LoadingModList into an interface with static queries.
			if (LoadingModList.class.isInterface())
			{
				return Optional.ofNullable((IModFileInfo)LoadingModList.class.getMethod("getModFileById", String.class).invoke(null, modId));
			}
			return Optional.ofNullable(LoadingModList.get()).map(ml -> ml.getModFileById(modId));
		}
		catch (Exception e)
		{
			return Optional.empty();
		}
	}

	private static Optional<IModFileInfo> getModFileInfo(String modId)
	{
		Optional<IModFileInfo> mod = getLoadingModFileInfo(modId);
		if (!mod.isPresent())
		{
			try
			{
				Method query = ModList.class.getMethod("getModFileById", String.class);

				// Forge 26.1+ exposes a static query on ModList.
				if (Modifier.isStatic(query.getModifiers()))
				{
					mod = Optional.ofNullable((IModFileInfo)query.invoke(null, modId));
				}
				else
				{
					mod = Optional.ofNullable(ModList.get()).map(ml -> ml.getModFileById(modId));
				}
			}
			catch (ReflectiveOperationException e)
			{
				throw new IllegalStateException("Failed to query Forge ModList for mod " + modId, e);
			}
		}
		return mod;
	}

	private static Optional<ArtifactVersion> getModVersion(IModFileInfo modFileInfo)
	{
		// IModFileInfo#versionString might not exist in old forge loader
		for (IModInfo mod : modFileInfo.getMods())
		{
			return Optional.of(mod.getVersion());
		}
		return Optional.empty();
	}

	public static boolean isModPresent(String modId)
	{
		return getModFileInfo(modId).isPresent();
	}

	public static Optional<String> getModVersionString(String modId)
	{
		return getModFileInfo(modId)
				.filter(modFileInfo -> !modFileInfo.getMods().isEmpty())
				.flatMap(VersionCheckerImpl::getModVersion)
				.map(Objects::toString);
	}

	@Deprecated
	public static boolean doesVersionSatisfyPredicate(Object version, String versionPredicate)
	{
		throw new RuntimeException("doesVersionSatisfyPredicate only works in fabric platform, not in forge platform");
	}

	public static boolean doesModVersionSatisfyPredicate(String modId, String versionPredicate)
	{
		Optional<ArtifactVersion> versionOpt = getModFileInfo(modId)
				.filter(modFileInfo -> !modFileInfo.getMods().isEmpty())
				.flatMap(VersionCheckerImpl::getModVersion);
		if (!versionOpt.isPresent()) return false;
		ArtifactVersion version = versionOpt.get();

		try
		{
			// TODO: consistent version predicate parsing across loaders
			return VersionRange.createFromVersionSpec(versionPredicate).containsVersion(version);
		}
		catch (Exception e)
		{
			ConditionalMixinMod.LOGGER.error("Failed to parse version or version predicate {} {}: {}", version.toString(), versionPredicate, e);
		}
		return false;
	}
}
