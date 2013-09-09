package powercrystals.minefactoryreloaded.tile.machine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import powercrystals.minefactoryreloaded.gui.client.GuiBioFuelGenerator;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerBioFuelGenerator;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityLiquidGenerator;

public class TileEntityBioFuelGenerator extends TileEntityLiquidGenerator {
    public static final int fluidConsumedPerTick = 1;
    public static final int energyProducedPerConsumption = 160;
    public static final int ticksBetweenConsumption = 9;

    public TileEntityBioFuelGenerator() {
        super(Machine.BioFuelGenerator, fluidConsumedPerTick, energyProducedPerConsumption, ticksBetweenConsumption);
    }

    @Override
    protected FluidStack getFluidType() {
        return FluidRegistry.getFluidStack("biofuel", 1);
    }

    @Override
    public int getSizeInventory() {
        return 0;
    }

    @Override
    public String getGuiBackground() {
        return "biofuelgenerator.png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {
        return new GuiBioFuelGenerator(getContainer(inventoryPlayer), this);
    }

    @Override
    public ContainerBioFuelGenerator getContainer(InventoryPlayer inventoryPlayer) {
        return new ContainerBioFuelGenerator(this, inventoryPlayer);
    }
}
