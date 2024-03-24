package me.fallenbreath.conditionalmixin.api.util.forge;

import me.fallenbreath.conditionalmixin.ConditionalMixinMod;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;

import java.util.Optional;

public class VersionCheckerImpl
{
	public static boolean isModPresent(String modId)
	{
		return LoadingModList.get().getModFileById(modId) != null;
	}

	public static Optional<String> getModVersionString(String modId)
	{
		return Optional.ofNullable(LoadingModList.get().getModFileById(modId)).map(ModFileInfo::versionString);
	}

	public static boolean doesVersionSatisfyPredicate(Object version, String versionPredicate)
	{
		throw new RuntimeException("doesVersionSatisfyPredicate only works in fabric platform, not in forge platform");
	}

	public static boolean doesModVersionSatisfyPredicate(String modId, String versionPredicate)
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
