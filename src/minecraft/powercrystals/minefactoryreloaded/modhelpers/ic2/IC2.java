package powercrystals.minefactoryreloaded.modhelpers.ic2;


import ic2.api.item.Items;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.Recipes;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import powercrystals.minefactoryreloaded.MFRRegistry;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.HarvestType;
import powercrystals.minefactoryreloaded.farmables.fertilizables.FertilizerStandard;
import powercrystals.minefactoryreloaded.farmables.harvestables.HarvestableTreeLeaves;
import powercrystals.minefactoryreloaded.farmables.plantables.PlantableStandard;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "MineFactoryReloaded|CompatIC2", name = "MFR Compat: IC2", version = MineFactoryReloadedCore.version, dependencies = "after:MineFactoryReloaded;after:IC2")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class IC2
{
	@EventHandler
	public static void load(FMLInitializationEvent e)
	{
		if(!Loader.isModLoaded("IC2"))
		{
			FMLLog.warning("IC2 missing - MFR IC2 Compat not loading");
			return;
		}
		try
		{
			ItemStack crop = Items.getItem("crop");
			ItemStack rubber = Items.getItem("rubber");
			ItemStack rubberSapling = Items.getItem("rubberSapling");
			ItemStack rubberLeaves = Items.getItem("rubberLeaves");
			ItemStack rubberWood = Items.getItem("rubberWood");
			ItemStack stickyResin = Items.getItem("resin");
			ItemStack plantBall = Items.getItem("plantBall");
			
			if(rubberSapling != null)
			{
				MFRRegistry.registerPlantable(new PlantableStandard(rubberSapling.itemID, rubberSapling.itemID));
				MFRRegistry.registerFertilizable(new FertilizableIC2RubberTree(rubberSapling.itemID));
			}
			if(rubberLeaves != null)
			{
				MFRRegistry.registerHarvestable(new HarvestableTreeLeaves(rubberLeaves.itemID));
			}
			if(rubberWood != null)
			{
				MFRRegistry.registerHarvestable(new HarvestableIC2RubberWood(rubberWood.itemID, HarvestType.Tree, stickyResin.itemID));
				MFRRegistry.registerFruitLogBlockId(((ItemBlock)rubberWood.getItem()).getBlockID());
				MFRRegistry.registerFruit(new FruitIC2Resin(rubberWood, stickyResin));
			}
			
			ItemStack fertilizer = Items.getItem("fertilizer");
			if(fertilizer != null)
			{
				MFRRegistry.registerFertilizer(new FertilizerStandard(fertilizer.itemID, fertilizer.getItemDamage()));
			}
			
			MFRRegistry.registerHarvestable(new HarvestableIC2Crop(crop.itemID));
			
			GameRegistry.addShapedRecipe(plantBall, new Object[]
					{
					"LLL",
					"L L",
					"LLL",
					Character.valueOf('L'), new ItemStack(MineFactoryReloadedCore.rubberLeavesBlock)
					} );
			
			Recipes.extractor.addRecipe(new RecipeInputItemStack(new ItemStack(MineFactoryReloadedCore.rubberSaplingBlock)), null, rubber);
		}
		catch (Exception x)
		{
			x.printStackTrace();
		}
	}
}
