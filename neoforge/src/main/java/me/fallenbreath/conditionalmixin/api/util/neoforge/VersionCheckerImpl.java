package me.fallenbreath.conditionalmixin.api.util.neoforge;

import me.fallenbreath.conditionalmixin.ConditionalMixinMod;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.neoforgespi.language.IModFileInfo;
import net.neoforged.neoforgespi.language.IModInfo;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;

import java.util.Objects;
import java.util.Optional;

public class VersionCheckerImpl
{
	private static Optional<IModFileInfo> getModFileInfo(String modId)
	{
		IModFileInfo mod = LoadingModList.get().getModFileById(modId);
		if (mod == null)
		{
			mod = ModList.get().getModFileById(modId);
		}
		return Optional.ofNullable(mod);
	}

	private static Optional<ArtifactVersion> getModVersion(IModFileInfo modFileInfo)
	{
		// be consistent with forge impl
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
		throw new RuntimeException("doesVersionSatisfyPredicate only works in fabric platform, not in neoforge platform");
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
