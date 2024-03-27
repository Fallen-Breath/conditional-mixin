package me.fallenbreath.conditionalmixin.api.mixin;

@FunctionalInterface
public interface RestrictionCheckFailureCallback
{
	void callback(String mixinClassName, String reason);
}
