package powercrystals.minefactoryreloaded.render.item;

import cpw.mods.fml.client.registry.RenderingRegistry;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.block.BlockFactoryGlassPane;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

public class FactoryGlassPaneItemRenderer implements IItemRenderer
{
    private static ResourceLocation loc = new ResourceLocation("%blur%/misc/glint.png");

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type)
	{
		return type.ordinal() < ItemRenderType.FIRST_PERSON_MAP.ordinal();
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
	{
		return helper.ordinal() < ItemRendererHelper.EQUIPPED_BLOCK.ordinal();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data)
	{
		TextureManager renderEngine = Minecraft.getMinecraft().renderEngine;
		RenderBlocks renderer = (RenderBlocks)data[0];

		BlockFactoryGlassPane pane = (BlockFactoryGlassPane)Block.blocksList[((ItemBlock)item.getItem()).getBlockID()];

		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_CULL_FACE);

		Tessellator tessellator = Tessellator.instance;

		if(type == ItemRenderType.INVENTORY)
		{
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glScalef(16f, 16f, 16f);
			GL11.glTranslatef(0.5f, 0.5f, 0.5f);

			RenderingRegistry.instance().renderInventoryBlock(renderer, pane, item.getItemDamage(), MineFactoryReloadedCore.renderIdFactoryGlassPane);

			GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
			GL11.glScalef(1 / 16f, 1 / 16f, 1 / 16f);

			if (item.hasEffect())
			{
				GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glDepthFunc(GL11.GL_GREATER);
				GL11.glDepthMask(false);
				renderEngine.bindTexture(loc);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_DST_COLOR);
				GL11.glColor4f(0.5F, 0.25F, 0.8F, 1.0F);
				GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
				float f = 0.00390625F;
				float f1 = 0.00390625F;
				float f2 = Minecraft.getSystemTime() % (3000 + 0 * 1873) / (3000.0F + 0 * 1873) * 256.0F;
				float f3 = 0.0F;
				float f4 = 4.0F;
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(0,  16, -50, (f2 + 16 * f4) * f, (f3 + 16) * f1);
				tessellator.addVertexWithUV(16, 16, -50, (f2 + 16 + 16 * f4) * f, (f3 + 16) * f1);
				tessellator.addVertexWithUV(16, 0,  -50, (f2 + 16) * f, (f3 + 0.0F) * f1);
				tessellator.addVertexWithUV(0,  0,  -50, (f2 + 0.0F) * f, (f3 + 0.0F) * f1);
				tessellator.draw();
				f2 = Minecraft.getSystemTime() % (3000 + 1 * 1873) / (3000.0F + 1 * 1873) * 256.0F;
				f4 = -1.0F;
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(0,  16, -50, (f2 + 16 * f4) * f, (f3 + 16) * f1);
				tessellator.addVertexWithUV(16, 16, -50, (f2 + 16 + 16 * f4) * f, (f3 + 16) * f1);
				tessellator.addVertexWithUV(16, 0,  -50, (f2 + 16) * f, (f3 + 0.0F) * f1);
				tessellator.addVertexWithUV(0,  0,  -50, (f2 + 0.0F) * f, (f3 + 0.0F) * f1);
				tessellator.draw();
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glDepthMask(true);
				GL11.glDepthFunc(GL11.GL_LEQUAL);
			}
			GL11.glEnable(GL11.GL_LIGHTING);
		}
		else
		{
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			
			switch (type)
			{
			case EQUIPPED_FIRST_PERSON:
			case EQUIPPED:
				GL11.glTranslatef(10 / 16f, 7 / 16f, 0f);
				break;
			case ENTITY:
				GL11.glScalef(0.75f, 0.75f, 0.75f);
	            GL11.glTranslatef(0f, 4 / 16f, 0f);
				break;
			default:
			}

			RenderingRegistry.instance().renderInventoryBlock(renderer, pane, item.getItemDamage(), MineFactoryReloadedCore.renderIdFactoryGlassPane);

			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		}
		
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}
}
