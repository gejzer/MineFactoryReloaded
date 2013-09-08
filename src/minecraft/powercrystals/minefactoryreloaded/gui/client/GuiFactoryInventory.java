package powercrystals.minefactoryreloaded.gui.client;

import cpw.mods.fml.common.network.PacketDispatcher;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import powercrystals.core.net.PacketWrapper;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryInventory;
import powercrystals.minefactoryreloaded.gui.slot.SlotFake;
import powercrystals.minefactoryreloaded.net.Packets;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;

import java.util.ArrayList;
import java.util.List;

public class GuiFactoryInventory extends GuiContainer
{
    private ResourceLocation loc;
    protected ResourceLocation fluid;

	protected TileEntityFactoryInventory _tileEntity;
	protected int _barSizeMax = 60;
	protected int _tankSizeMax = 60;
	
	public GuiFactoryInventory(ContainerFactoryInventory container, TileEntityFactoryInventory tileentity)
	{
		super(container);
		_tileEntity = tileentity;
        loc = new ResourceLocation(MineFactoryReloadedCore.guiFolder + _tileEntity.getGuiBackground());
	}
	
	@Override
	protected void mouseClicked(int x, int y, int button)
	{
		super.mouseClicked(x, y, button);
		
		x -= guiLeft;
		y -= guiTop;
		
		for(Object o : inventorySlots.inventorySlots)
		{
			if(!(o instanceof SlotFake))
			{
				continue;
			}
			SlotFake s = (SlotFake)o;
			if(x >= s.xDisplayPosition && x <= s.xDisplayPosition + 16 && y >= s.yDisplayPosition && y <= s.yDisplayPosition + 16)
			{
				PacketDispatcher.sendPacketToServer(PacketWrapper.createPacket(MineFactoryReloadedCore.modNetworkChannel, Packets.FakeSlotChange,
						new Object[] { _tileEntity.xCoord, _tileEntity.yCoord, _tileEntity.zCoord, s.slotNumber }));
			}
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRenderer.drawString(_tileEntity.getInvName(), 8, 6, 4210752);
		fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		if(_tileEntity.getTank() != null && _tileEntity.getTank().getFluid() != null)
		{
			int tankSize = _tileEntity.getTank().getFluidAmount() * _tankSizeMax / _tileEntity.getTank().getCapacity();
			drawTank(122, 75, tankSize, _tileEntity.getTank().getFluid());
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float gameTicks, int mouseX, int mouseY)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(loc);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float gameTicks)
	{
		super.drawScreen(mouseX, mouseY, gameTicks);
		
		drawTooltips(mouseX, mouseY);
	}
	
	protected void drawTooltips(int mouseX, int mouseY)
	{
		if(isPointInRegion(122, 15, 16, 60, mouseX, mouseY) && _tileEntity.getTank() != null && _tileEntity.getTank().getFluidAmount() > 0)
		{
            drawBarTooltip(_tileEntity.getTank().getFluid().getFluid().getLocalizedName(),
                    "mB", _tileEntity.getTank().getFluidAmount(), _tileEntity.getTank().getCapacity(), mouseX, mouseY);
		}
	}
	
	protected void drawBar(int xOffset, int yOffset, int max, int current, int color)
	{
		int size = max > 0 ? current * _barSizeMax / max : 0;
		if(size > _barSizeMax) size = max;
		if(size < 0) size = 0;
		drawRect(xOffset, yOffset - size, xOffset + 8, yOffset, color);
	}
	
	protected void drawTank(int xOffset, int yOffset, int level, FluidStack stack)
    {
        if(stack == null || stack.getFluid() == null)
			return;
		
		Icon icon = stack.getFluid().getStillIcon();
		if (icon == null)
            icon = Block.lavaMoving.getIcon(0, 0);
		
		int vertOffset = 0;

        if (fluid == null)
        {
            String name = icon.getIconName() + ".png";
            if (name.contains(":"))
            {
                String[] split = name.split(":");
                fluid = new ResourceLocation(split[0] + ":textures/blocks/" + split[1]);
            } else
                fluid = new ResourceLocation("textures/blocks/" + name);
        }

        mc.renderEngine.bindTexture(fluid);

		while(level > 0)
		{
			int texHeight;
			
			if(level > 16)
			{
				texHeight = 16;
				level -= 16;
			}
			else
			{
				texHeight = level;
				level = 0;
			}

			drawTexturedModelRectFromIcon(xOffset, yOffset - texHeight - vertOffset, icon, 16, texHeight);
			vertOffset = vertOffset + 16;
		}
		
		this.mc.renderEngine.bindTexture(loc);
		this.drawTexturedModalRect(xOffset, yOffset - 60, 176, 0, 16, 60);
	}
	
	protected void drawBarTooltip(String name, String unit, int value, int max, int x, int y)
	{
		List<String> lines = new ArrayList<String>();
		lines.add(name);
		lines.add(value + " / " + max + " " + unit);
		drawTooltip(lines, x, y);
	}
	
	protected void drawTooltip(List<String> lines, int x, int y)
	{
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glDisable(GL11.GL_LIGHTING);
		
		int tooltipWidth = 0;
		int tempWidth;
		int xStart;
		int yStart;
		
		for(int i = 0; i < lines.size(); i++)
		{
			tempWidth = this.fontRenderer.getStringWidth(lines.get(i));
			
			if(tempWidth > tooltipWidth)
			{
				tooltipWidth = tempWidth;
			}
		}
		
		xStart = x + 12;
		yStart = y - 12;
		int tooltipHeight = 8;
		
		if(lines.size() > 1)
		{
			tooltipHeight += 2 + (lines.size() - 1) * 10;
		}
		
		if(this.guiTop + yStart + tooltipHeight + 6 > this.height)
		{
			yStart = this.height - tooltipHeight - this.guiTop - 6;
		}
		
		this.zLevel = 300.0F;
		itemRenderer.zLevel = 300.0F;
		int color1 = -267386864;
		this.drawGradientRect(xStart - 3, yStart - 4, xStart + tooltipWidth + 3, yStart - 3, color1, color1);
		this.drawGradientRect(xStart - 3, yStart + tooltipHeight + 3, xStart + tooltipWidth + 3, yStart + tooltipHeight + 4, color1, color1);
		this.drawGradientRect(xStart - 3, yStart - 3, xStart + tooltipWidth + 3, yStart + tooltipHeight + 3, color1, color1);
		this.drawGradientRect(xStart - 4, yStart - 3, xStart - 3, yStart + tooltipHeight + 3, color1, color1);
		this.drawGradientRect(xStart + tooltipWidth + 3, yStart - 3, xStart + tooltipWidth + 4, yStart + tooltipHeight + 3, color1, color1);
		int color2 = 1347420415;
		int color3 = (color2 & 16711422) >> 1 | color2 & -16777216;
		this.drawGradientRect(xStart - 3, yStart - 3 + 1, xStart - 3 + 1, yStart + tooltipHeight + 3 - 1, color2, color3);
		this.drawGradientRect(xStart + tooltipWidth + 2, yStart - 3 + 1, xStart + tooltipWidth + 3, yStart + tooltipHeight + 3 - 1, color2, color3);
		this.drawGradientRect(xStart - 3, yStart - 3, xStart + tooltipWidth + 3, yStart - 3 + 1, color2, color2);
		this.drawGradientRect(xStart - 3, yStart + tooltipHeight + 2, xStart + tooltipWidth + 3, yStart + tooltipHeight + 3, color3, color3);
		
		for(int stringIndex = 0; stringIndex < lines.size(); ++stringIndex)
		{
			String line = lines.get(stringIndex);
			
			if(stringIndex == 0)
			{
				line = "\u00a7" + Integer.toHexString(15) + line;
			}
			else
			{
				line = "\u00a77" + line;
			}
			
			this.fontRenderer.drawStringWithShadow(line, xStart, yStart, -1);
			
			if(stringIndex == 0)
			{
				yStart += 2;
			}
			
			yStart += 10;
		}
		
		GL11.glPopMatrix();
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		this.zLevel = 0.0F;
		itemRenderer.zLevel = 0.0F;
	}
}
