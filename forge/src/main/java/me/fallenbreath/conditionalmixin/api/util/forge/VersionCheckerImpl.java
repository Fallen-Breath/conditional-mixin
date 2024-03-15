package me.fallenbreath.conditionalmixin.api.util.forge;

import me.fallenbreath.conditionalmixin.ConditionalMixinMod;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;

import java.util.Optional;

public class VersionCheckerImpl {
    public static boolean isModPresent(String modId)
    {
		return ModList.get().isLoaded(modId);
    }

    public static String getVersionString(String modId)
    {
		return ModList.get().getModContainerById(modId).orElseThrow(IllegalArgumentException::new).getModInfo().getVersion().toString();
    }

    public static boolean doesVersionSatisfyPredicate(String modId, String versionPredicate)
    {
        Optional<? extends ModContainer> modContainer = ModList.get().getModContainerById(modId);
        if (!modContainer.isPresent()) return false;
		ArtifactVersion version = modContainer.get().getModInfo().getVersion();
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
