package me.fallenbreath.conditionalmixin.api.util.forge;

import me.fallenbreath.conditionalmixin.ConditionalMixinMod;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;

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
			mod = Optional.ofNullable(ModList.get()).map(ml -> ml.getModFileById(modId));
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
