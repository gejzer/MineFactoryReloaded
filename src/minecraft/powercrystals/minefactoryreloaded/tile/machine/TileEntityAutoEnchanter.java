package powercrystals.minefactoryreloaded.tile.machine;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.*;
import powercrystals.minefactoryreloaded.core.AutoEnchantmentHelper;
import powercrystals.minefactoryreloaded.gui.client.GuiAutoEnchanter;
import powercrystals.minefactoryreloaded.gui.client.GuiFactoryInventory;
import powercrystals.minefactoryreloaded.gui.container.ContainerAutoEnchanter;
import powercrystals.minefactoryreloaded.setup.Machine;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryPowered;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

public class TileEntityAutoEnchanter extends TileEntityFactoryPowered implements IFluidHandler {
    private Random _rand;
    private int _targetLevel;
    private FluidTank _tank;

    public TileEntityAutoEnchanter() {
        super(Machine.AutoEnchanter);
        _rand = new Random();

        _targetLevel = 30;
        _tank = new FluidTank(4 * FluidContainerRegistry.BUCKET_VOLUME);
    }

    @Override
    public String getGuiBackground() {
        return "autoenchanter.png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiFactoryInventory getGui(InventoryPlayer inventoryPlayer) {
        return new GuiAutoEnchanter(getContainer(inventoryPlayer), this);
    }

    @Override
    public ContainerAutoEnchanter getContainer(InventoryPlayer inventoryPlayer) {
        return new ContainerAutoEnchanter(this, inventoryPlayer);
    }

    @Override
    public int getWorkMax() {
        if (getStackInSlot(0) != null && getStackInSlot(0).itemID == Item.glassBottle.itemID) {
            return 250;
        }
        return (_targetLevel + (int) (Math.pow((_targetLevel) / 7.5, 4) * 10 * getEnchantmentMultiplier()));
    }

    @SuppressWarnings("unchecked")
    private double getEnchantmentMultiplier() {
        ItemStack s = getStackInSlot(0);
        if (s == null) {
            return 1;
        }

        Map<Integer, EnchantmentData> enchantments = AutoEnchantmentHelper.getEnchantments(s);
        if (enchantments == null || enchantments.size() == 0) {
            return 1;
        }

        return Math.pow(enchantments.size() + 1.0, 2);
    }

    @Override
    public int getEnergyStoredMax() {
        return 16000;
    }

    @Override
    public int getIdleTicksMax() {
        return 1;
    }

    public int getTargetLevel() {
        return _targetLevel;
    }

    public void setTargetLevel(int targetLevel) {
        _targetLevel = targetLevel;
        if (_targetLevel > 30) _targetLevel = 30;
        if (_targetLevel < 1) _targetLevel = 1;
        if (getWorkDone() >= getWorkMax()) {
            activateMachine();
        }
    }

    @Override
    public void updateServer(DataInputStream stream, EntityPlayerMP player) throws IOException {
        setTargetLevel(getTargetLevel() + stream.readInt());
    }

    @Override
    public FluidTank getTank() {
        return _tank;
    }

    @Override
    protected boolean activateMachine() {
        if (worldObj.isRemote) {
            return false;
        }
        ItemStack input = getStackInSlot(0);
        ItemStack output = getStackInSlot(1);
        if (input == null) {
            setWorkDone(0);
            return false;
        }
        if (input.stackSize <= 0) {
            setInventorySlotContents(0, null);
            setWorkDone(0);
            return false;
        }
        if (output != null) {
            if (output.stackSize >= output.getMaxStackSize() || output.stackSize >= getInventoryStackLimit()) {
                setWorkDone(0);
                return false;
            }
            if (output.stackSize <= 0) {
                setInventorySlotContents(1, null);
                output = null;
            }
        }
        if ((input.getItem().getItemEnchantability() == 0 && input.itemID != Item.glassBottle.itemID) || input.itemID == Item.enchantedBook.itemID) {
            if (output == null) {
                setInventorySlotContents(0, null);
                setInventorySlotContents(1, input);
            } else if (input.isItemEqual(output) && ItemStack.areItemStackTagsEqual(input, output)) {
                int amountToCopy = Math.min(output.getMaxStackSize() - output.stackSize, input.stackSize);
                amountToCopy = Math.min(getInventoryStackLimit() - output.stackSize, amountToCopy);
                if (amountToCopy <= 0) {
                    setWorkDone(0);
                    return false;
                }
                output.stackSize += amountToCopy;
                input.stackSize -= amountToCopy;
                if (input.stackSize <= 0) {
                    setInventorySlotContents(0, null);
                }
            } else {
                setWorkDone(0);
                return false;
            }
            setWorkDone(0);
            return true;
        } else if (getWorkDone() >= getWorkMax()) {
            if (input.itemID == Item.glassBottle.itemID) {
                if (output == null) {
                    output = new ItemStack(Item.expBottle, 0, 0);
                }
                if (output.itemID != Item.expBottle.itemID) {
                    setWorkDone(0);
                    return false;
                }
                if (--input.stackSize <= 0) {
                    setInventorySlotContents(0, null);
                }
                output.stackSize++;
                setInventorySlotContents(1, output);
                setWorkDone(0);
            } else if (output == null) {
                output = AutoEnchantmentHelper.addRandomEnchantment(this._rand, input, _targetLevel);
                if (input.stackSize <= 0) {
                    setInventorySlotContents(0, null);
                }
                setInventorySlotContents(1, output);
                setWorkDone(0);
            } else {
                return false;
            }
            return true;
        } else if (_tank.getFluid() != null && _tank.getFluid().amount >= 4) {
            _tank.drain(4, true);
            setWorkDone(getWorkDone() + 1);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getSizeInventory() {
        return 2;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public int getSizeInventorySide(ForgeDirection side) {
        return 2;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack input, int sideordinal) {
        if (slot == 0) {
            ItemStack contents = this.getStackInSlot(0);
            // TODO: limit input to glass bottles and items with an enchantability > 0
            return contents == null || (contents.stackSize < getInventoryStackLimit() &&
                    input.isItemEqual(contents) && ItemStack.areItemStackTagsEqual(input, contents));
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack itemstack, int sideordinal) {
        return slot == 1;
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        nbttagcompound.setInteger("targetLevel", _targetLevel);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        _targetLevel = nbttagcompound.getInteger("targetLevel");
    }

    @Override
    public boolean allowBucketFill() {
        return true;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        if (resource == null || (resource.fluidID != FluidRegistry.getFluidID("essence")))
            return 0;
        return _tank.fill(resource, doFill);
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[] { _tank.getInfo() };
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
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return (fluid != null) && (fluid.getID() == FluidRegistry.getFluidID("essence"));
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return false;
    }
}
