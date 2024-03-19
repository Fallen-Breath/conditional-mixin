package me.fallenbreath.conditionalmixin.api.util.neoforge;

import me.fallenbreath.conditionalmixin.ConditionalMixinMod;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;

import java.util.Optional;

public class VersionCheckerImpl
{
	public static boolean isModPresent(String modId)
	{
		return LoadingModList.get().getModFileById(modId) != null;
	}

	public static Optional<String> getVersionString(String modId)
	{
		return Optional.ofNullable(LoadingModList.get().getModFileById(modId)).map(ModFileInfo::versionString);
	}

	public static boolean doesVersionSatisfyPredicate(String modId, String versionPredicate)
	{
		ModFileInfo modInfo = LoadingModList.get().getModFileById(modId);
		if (modInfo == null) return false;
		ArtifactVersion version = modInfo.getMods().get(0).getVersion();
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
