package powercrystals.minefactoryreloaded.farmables.drinkhandlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import powercrystals.minefactoryreloaded.api.IFluidDrinkHandler;

public class DrinkHandlerBiofuel implements IFluidDrinkHandler
{
	@Override
	public void onDrink(EntityPlayer player)
	{
		player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 40 * 20, 0));
		player.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 40 * 20, 0));
	}
}
