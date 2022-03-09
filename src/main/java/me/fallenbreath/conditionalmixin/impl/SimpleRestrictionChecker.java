package me.fallenbreath.conditionalmixin.impl;

import com.google.common.collect.Lists;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.conditionalmixin.api.mixin.RestrictionCheckFailureCallback;
import me.fallenbreath.conditionalmixin.api.mixin.RestrictionChecker;
import me.fallenbreath.conditionalmixin.api.util.VersionChecker;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A restriction checker implementation
 */
public class SimpleRestrictionChecker implements RestrictionChecker
{
	private RestrictionCheckFailureCallback failureCallback = null;

	@Override
	public boolean checkRestriction(String mixinClassName)
	{
		AnnotationNode restriction = getRestrictionAnnotation(mixinClassName);
		if (restriction != null)
		{
			List<AnnotationNode> enableConditions = Annotations.getValue(restriction, "require", true);
			for (Result result : this.checkConditions(enableConditions))
			{
				if (!result.success)
				{
					this.onRestrictionCheckFailure(mixinClassName, result.reason);
					return false;
				}
			}
			List<AnnotationNode> disableConditions = Annotations.getValue(restriction, "conflict", true);
			for (Result result : this.checkConditions(disableConditions))
			{
				if (result.success)
				{
					this.onRestrictionCheckFailure(mixinClassName, result.reason);
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public void setFailureCallback(RestrictionCheckFailureCallback failureCallback)
	{
		this.failureCallback = failureCallback;
	}

	@Nullable
	private AnnotationNode getRestrictionAnnotation(String className)
	{
		try
		{
			ClassNode classNode = MixinService.getService().getBytecodeProvider().getClassNode(className);
			return Annotations.getVisible(classNode, Restriction.class);
		}
		catch (ClassNotFoundException | IOException e)
		{
			return null;
		}
	}

	private List<Result> checkConditions(List<AnnotationNode> conditions)
	{
		List<Result> results = Lists.newArrayList();
		for (AnnotationNode condition : conditions)
		{
			Condition.Type type = Annotations.getValue(condition, "type", Condition.Type.class, Condition.Type.MOD);
			switch (type)
			{
				case MOD:
					String modId = Annotations.getValue(condition, "value");
					Objects.requireNonNull(modId);
					Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(modId);
					if (!modContainer.isPresent())
					{
						results.add(new Result(false, String.format("required mod %s not found", modId)));
						continue;
					}
					Version modVersion = modContainer.get().getMetadata().getVersion();
					List<String> versionPredicates = Lists.newArrayList(Annotations.getValue(condition, "versionPredicates", Lists.newArrayList()));
					if (!VersionChecker.doesVersionSatisfyPredicate(modVersion, versionPredicates))
					{
						results.add(new Result(false, String.format("mod %s@%s does not matches version predicates %s", modId, modVersion.getFriendlyString(), versionPredicates)));
						continue;
					}
					results.add(new Result(true, String.format("conflicted/unsupported mod %s@%s found", modId, modVersion.getFriendlyString())));
					break;

				case MIXIN:
					String className = Annotations.getValue(condition, "value");
					if (!this.checkRestriction(className))
					{
						results.add(new Result(false, String.format("required mixin class %s disabled", className)));
						continue;
					}
					results.add(new Result(true, String.format("conflicted mixin class %s found", className)));
					break;
			}
		}
		return results;
	}

	private void onRestrictionCheckFailure(String mixinClassName, String reason)
	{
		if (this.failureCallback != null)
		{
			this.failureCallback.callback(mixinClassName, reason);
		}
	}

	private static class Result
	{
		public final boolean success;
		public final String reason;

		private Result(boolean success, String reason)
		{
			this.success = success;
			this.reason = reason;
		}
	}
}
