package me.fallenbreath.conditionalmixin.api.mixin;

import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.conditionalmixin.api.checker.RestrictionChecker;
import me.fallenbreath.conditionalmixin.api.checker.RestrictionCheckers;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * A simple class that handle the {@link me.fallenbreath.conditionalmixin.api.annotation.Restriction} annotation
 * Make your custom MixinConfigPlugin extend this class and that's it, you have conditional mixin now
 */
public abstract class RestrictiveMixinConfigPlugin implements IMixinConfigPlugin
{
	protected final RestrictionChecker restrictionChecker = RestrictionCheckers.memorized();
	private final AnnotationCleaner annotationCleaner = AnnotationCleaner.create(Restriction.class);

	public RestrictiveMixinConfigPlugin()
	{
		this.restrictionChecker.setFailureCallback(this::onRestrictionCheckFailed);
	}

	@Override
	public void onLoad(String mixinPackage)
	{
		// in case
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
	{
		return this.restrictionChecker.checkRestriction(mixinClassName);
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
	{
		this.annotationCleaner.onPreApply(targetClass);
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo)
	{
		this.annotationCleaner.onPostApply(targetClass);
	}

	/**
	 * go override it and do something you want, e.g. logging
	 */
	protected void onRestrictionCheckFailed(String mixinClassName, String reason)
	{
	}
}
