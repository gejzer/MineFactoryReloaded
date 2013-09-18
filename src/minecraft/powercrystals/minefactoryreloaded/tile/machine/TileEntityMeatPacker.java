package powercrystals.minefactoryreloaded.tile.machine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.*;
import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryPowered;
import powercrystals.minefactoryreloaded.gui.container.ContainerFactoryPowered;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

public class TileEntityMeatPacker extends TileEntityFactoryPowered implements IFluidHandler {
    private FluidTank _tank;

    public TileEntityMeatPacker() {
        super(Machine.MeatPacker);
        _tank = new FluidTank(4 * FluidContainerRegistry.BUCKET_VOLUME);
    }

    @Override
    public int getSizeInventory() {
        return 0;
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return false;
    }

    @Override
    public String getGuiBackground() {
        return "meatpacker.png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {
        return new GuiFactoryPowered(getContainer(inventoryPlayer), this);
    }

    @Override
    public ContainerFactoryPowered getContainer(InventoryPlayer inventoryPlayer) {
        return new ContainerFactoryPowered(this, inventoryPlayer);
    }

    @Override
    protected boolean activateMachine() {
        if (_tank.getFluid() != null && _tank.getFluidAmount() >= 2) {
            setWorkDone(getWorkDone() + 1);

            if (getWorkDone() >= getWorkMax()) {
                ItemStack item;
                if (_tank.getFluid().isFluidEqual(FluidRegistry.getFluidStack("meat", 0))) {
                    item = new ItemStack(MineFactoryReloadedCore.meatIngotRawItem);
                } else {
                    item = new ItemStack(MineFactoryReloadedCore.meatNuggetRawItem);
                }

                doDrop(item);

                setWorkDone(0);
            }
            _tank.drain(2, true);
            return true;
        }
        return false;
    }

    @Override
    public int getEnergyStoredMax() {
        return 16000;
    }

    @Override
    public int getWorkMax() {
        return 50;
    }

    @Override
    public int getIdleTicksMax() {
        return 0;
    }

    @Override
    public boolean allowBucketFill() {
        return true;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (resource == null || !(resource.isFluidEqual(FluidRegistry.getFluidStack("meat", 0)) || resource.isFluidEqual(FluidRegistry.getFluidStack("pinkslime", 0)))) {
            return 0;
        } else {
            if (_tank.getFluidAmount() == 1) {
                _tank.drain(1, true);
            }
            return _tank.fill(resource, doFill);
        }
    }

    @Override
    public FluidTank getTank() {
        return _tank;
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return null;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return (fluid != null) && (fluid.getID() == FluidRegistry.getFluidID("meat"));
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { _tank.getInfo() };
    }

    @Override
    public boolean manageSolids() {
        return true;
    }
}
