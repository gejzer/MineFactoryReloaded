package powercrystals.minefactoryreloaded.render.tileentity;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;
import powercrystals.minefactoryreloaded.tile.machine.TileEntityLaserDrill;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LaserDrillRenderer extends TileEntitySpecialRenderer
{
	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks)
	{
		TileEntityLaserDrill laserDrill = (TileEntityLaserDrill)tileEntity;
		if(laserDrill.shouldDrawBeam())
		{
			this.bindTexture(new ResourceLocation("/misc/beam.png"));
			LaserRendererBase.renderLaser(laserDrill, x, y, z, laserDrill.getBeamHeight(), ForgeDirection.DOWN, partialTicks);
		}
	}
}