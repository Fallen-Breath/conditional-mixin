package me.fallenbreath.conditionalmixin;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConditionalMixinMod implements ModInitializer
{
	public static final String MOD_ID = "conditional-mixin";

	public static final Logger LOGGER = LogManager.getLogger(ConditionalMixinMod.class);

	@Override
	public void onInitialize()
	{
	}
}
