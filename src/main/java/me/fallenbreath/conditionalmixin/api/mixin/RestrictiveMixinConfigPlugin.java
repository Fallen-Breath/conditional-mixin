package me.fallenbreath.conditionalmixin.api.mixin;

import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;

/**
 * A simple class that handle the {@link me.fallenbreath.conditionalmixin.api.annotation.Restriction} annotation
 * Make your custom MixinConfigPlugin extends this class and that's it, you have conditional mixin now
 */
public abstract class RestrictiveMixinConfigPlugin implements IMixinConfigPlugin
{
	protected final RestrictionChecker restrictionChecker = new RestrictionChecker();

	public RestrictiveMixinConfigPlugin()
	{
		this.restrictionChecker.setFailureCallback(this::onRestrictionCheckFailed);
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName)
	{
		return this.restrictionChecker.checkRestriction(mixinClassName);
	}

	/**
	 * go override it and do something you want, e.g. logging
	 */
	protected void onRestrictionCheckFailed(String mixinClassName, String reason)
	{
	}
}
