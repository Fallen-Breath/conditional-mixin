package me.fallenbreath.conditionalmixin.impl;

import com.google.common.collect.Lists;
import me.fallenbreath.conditionalmixin.ConditionalMixinMod;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.conditionalmixin.api.checker.RestrictionChecker;
import me.fallenbreath.conditionalmixin.api.mixin.ConditionTester;
import me.fallenbreath.conditionalmixin.api.mixin.RestrictionCheckFailureCallback;
import me.fallenbreath.conditionalmixin.api.util.VersionChecker;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * A restriction checker implementation
 */
public class SimpleRestrictionChecker implements RestrictionChecker
{
	private RestrictionCheckFailureCallback failureCallback = null;

	@Override
	public boolean checkRestriction(String mixinClassName)
	{
		AnnotationNode restriction;
		try
		{
			restriction = getRestrictionAnnotation(mixinClassName);
		}
		catch (ClassNotFoundException e)
		{
			this.onRestrictionCheckFailure(mixinClassName, String.format("class '%s' not found", mixinClassName));
			return false;
		}

		if (restriction == null)
		{
			return true;
		}

		List<AnnotationNode> enableConditions = Annotations.getValue(restriction, "require", true);
		for (Result result : this.checkConditions(mixinClassName, enableConditions))
		{
			if (!result.success)
			{
				this.onRestrictionCheckFailure(mixinClassName, result.reason);
				return false;
			}
		}
		List<AnnotationNode> disableConditions = Annotations.getValue(restriction, "conflict", true);
		for (Result result : this.checkConditions(mixinClassName, disableConditions))
		{
			if (result.success)
			{
				this.onRestrictionCheckFailure(mixinClassName, result.reason);
				return false;
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
	private AnnotationNode getRestrictionAnnotation(String className) throws ClassNotFoundException
	{
		ClassNode classNode;
		try
		{
			classNode = MixinService.getService().getBytecodeProvider().getClassNode(className);
		}
		catch (ClassNotFoundException | IOException e)
		{
			throw new ClassNotFoundException();
		}
		return Annotations.getVisible(classNode, Restriction.class);
	}

	private List<Result> checkConditions(String mixinClassName, List<AnnotationNode> conditions)
	{
		List<Result> results = Lists.newArrayList();
		for (AnnotationNode condition : conditions)
		{
			Condition.Type type = Annotations.getValue(condition, "type", Condition.Type.class, Condition.Type.MOD);
			switch (type)
			{
				case MOD:
					String modId = Annotations.getValue(condition, "value");
					Objects.requireNonNull(modId, "field value is required for condition type MOD, as the mod ID");

					if (!VersionChecker.isModPresent(modId))
					{
						results.add(new Result(false, String.format("required mod %s not found", modId)));
						continue;
					}
					List<String> versionPredicates = Lists.newArrayList(Annotations.getValue(condition, "versionPredicates", Lists.newArrayList()));
					String versionString = VersionChecker.getVersionString(modId);
					if (!VersionChecker.doesVersionSatisfyPredicate(modId, versionPredicates))
					{
						results.add(new Result(false, String.format("mod %s@%s does not matches version predicates %s", modId, versionString, versionPredicates)));
						continue;
					}
					results.add(new Result(true, String.format("conflicted/unsupported mod %s@%s found", modId, versionString)));
					break;

				case MIXIN:
					String requiredMixinClassName = Annotations.getValue(condition, "value");
					Objects.requireNonNull(requiredMixinClassName, "field tester is required for condition type MIXIN, as the required mixin class name");
					if (!this.checkRestriction(requiredMixinClassName))
					{
						results.add(new Result(false, String.format("required mixin class %s disabled", requiredMixinClassName)));
						continue;
					}
					results.add(new Result(true, String.format("conflicted mixin class %s found", requiredMixinClassName)));
					break;

				case TESTER:
					Type clazzType = Annotations.getValue(condition, "tester");
					Objects.requireNonNull(clazzType, "field tester is required for condition type TESTER");

					ConditionTester tester;
					try
					{
						Class<?> clazz = Class.forName(clazzType.getClassName());
						if (clazz.isInterface())
						{
							ConditionalMixinMod.LOGGER.error("Tester class {} is a interface, but it should be a class", clazz.getName());
							continue;
						}
						tester = (ConditionTester)clazz.getConstructor().newInstance();
					}
					catch (Exception e)
					{
						ConditionalMixinMod.LOGGER.error("Failed to instantiate a ConditionTester from class {}: {}", clazzType.getClassName(), e);
						continue;
					}

					boolean testingResult = tester.isSatisfied(mixinClassName);
					results.add(new Result(testingResult, String.format("tester result = %s", testingResult)));
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
