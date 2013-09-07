package powercrystals.minefactoryreloaded.item;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;

public class ItemUpgrade extends ItemFactory
{
	private static String[] _upgradeNames = { "lapis", "iron", "tin", "copper", "bronze", "silver", "gold", "quartz", "diamond", "platinum", "emerald" };
	private static Icon[] _upgradeIcons = new Icon[_upgradeNames.length];
	
	public ItemUpgrade(int id)
	{
		super(id);
		setHasSubtypes(true);
		setMaxDamage(0);
		setMetaMax(10);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List infoList, boolean advancedTooltips)
	{
		super.addInformation(stack, player, infoList, advancedTooltips);
		infoList.add("Radius increase: " + getUpgradeLevel(stack));
	}
	
	public int getUpgradeLevel(ItemStack s)
	{
		return Math.min(s.getItemDamage(), 10) + 1;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack s)
	{
		int md = Math.min(s.getItemDamage(), _upgradeNames.length);
		return getUnlocalizedName() + "." + _upgradeNames[md];
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister ir)
	{
		for(int i = 0; i < _upgradeIcons.length; i++)
		{
			_upgradeIcons[i] = ir.registerIcon(MineFactoryReloadedCore.modId + ":" + getUnlocalizedName() + "." + _upgradeNames[i]);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIconFromDamage(int meta)
	{
		meta = Math.min(meta, _upgradeIcons.length);
		return _upgradeIcons[meta];
	}
}
