package powercrystals.minefactoryreloaded.gui.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import powercrystals.minefactoryreloaded.tile.base.TileEntityFactoryInventory;

public class ContainerFactoryInventory extends Container {
    protected TileEntityFactoryInventory _te;

    private int _tankAmount;
    private int _tankId;

    public ContainerFactoryInventory(TileEntityFactoryInventory tileentity, InventoryPlayer inv) {
        _te = tileentity;
        if (_te.getSizeInventory() > 0) {
            addSlots();
        }
        bindPlayerInventory(inv);
    }

    protected void addSlots() {
        addSlotToContainer(new Slot(_te, 0, 8, 15));
        addSlotToContainer(new Slot(_te, 1, 26, 15));
        addSlotToContainer(new Slot(_te, 2, 44, 15));
        addSlotToContainer(new Slot(_te, 3, 8, 33));
        addSlotToContainer(new Slot(_te, 4, 26, 33));
        addSlotToContainer(new Slot(_te, 5, 44, 33));
        addSlotToContainer(new Slot(_te, 6, 8, 51));
        addSlotToContainer(new Slot(_te, 7, 26, 51));
        addSlotToContainer(new Slot(_te, 8, 44, 51));
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();

        for (int i = 0; i < crafters.size(); i++) {
            ICrafting crafter = (ICrafting) crafters.get(i);
            if (_te.getTank() != null && _te.getTank().getFluid() != null) {
                crafter.sendProgressBarUpdate(this, 3, _te.getTank().getFluidAmount());
                crafter.sendProgressBarUpdate(this, 4, _te.getTank().getFluid().fluidID);
                crafter.sendProgressBarUpdate(this, 5, 0);
            } else if (_te.getTank() != null) {
                crafter.sendProgressBarUpdate(this, 3, 0);
                crafter.sendProgressBarUpdate(this, 4, 0);
                crafter.sendProgressBarUpdate(this, 5, 0);
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int var, int value) {
        super.updateProgressBar(var, value);

        if (var == 3) _tankAmount = value;
        else if (var == 4) _tankId = value;
        else if (var == 5) (_te.getTank()).setFluid(new FluidStack(_tankId, _tankAmount));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return _te.isUseableByPlayer(player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
        ItemStack stack = null;
        Slot slotObject = (Slot) inventorySlots.get(slot);
        int machInvSize = _te.getSizeInventory();

        if (slotObject != null && slotObject.getHasStack()) {
            ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();

            if (slot < machInvSize) {
                if (!mergeItemStack(stackInSlot, machInvSize, inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!mergeItemStack(stackInSlot, 0, machInvSize, false)) {
                return null;
            }

            if (stackInSlot.stackSize == 0) {
                slotObject.putStack(null);
            } else {
                slotObject.onSlotChanged();
            }

            if (stackInSlot.stackSize == stack.stackSize) {
                return null;
            }

            slotObject.onPickupFromSlot(player, stackInSlot);
        }

        return stack;
    }

    protected int getPlayerInventoryVerticalOffset() {
        return 84;
    }

    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, getPlayerInventoryVerticalOffset() + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, getPlayerInventoryVerticalOffset() + 58));
        }
    }

    @Override
    protected boolean mergeItemStack(ItemStack stack, int slotStart, int slotRange, boolean reverse) {
        boolean successful = false;
        int slotIndex = slotStart;
        int maxStack = Math.min(stack.getMaxStackSize(), _te.getInventoryStackLimit());

        if (reverse) {
            slotIndex = slotRange - 1;
        }

        Slot slot;
        ItemStack existingStack;

        if (stack.isStackable()) {
            while (stack.stackSize > 0 && (!reverse && slotIndex < slotRange || reverse && slotIndex >= slotStart)) {
                slot = (Slot) this.inventorySlots.get(slotIndex);
                existingStack = slot.getStack();

                if (slot.isItemValid(stack) && existingStack != null && existingStack.itemID == stack.itemID && (!stack.getHasSubtypes() || stack.getItemDamage() == existingStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, existingStack)) {
                    int existingSize = existingStack.stackSize + stack.stackSize;

                    if (existingSize <= maxStack) {
                        stack.stackSize = 0;
                        existingStack.stackSize = existingSize;
                        slot.onSlotChanged();
                        successful = true;
                    } else if (existingStack.stackSize < maxStack) {
                        stack.stackSize -= maxStack - existingStack.stackSize;
                        existingStack.stackSize = maxStack;
                        slot.onSlotChanged();
                        successful = true;
                    }
                }

                if (reverse) {
                    --slotIndex;
                } else {
                    ++slotIndex;
                }
            }
        }

        if (stack.stackSize > 0) {
            if (reverse) {
                slotIndex = slotRange - 1;
            } else {
                slotIndex = slotStart;
            }

            while (!reverse && slotIndex < slotRange || reverse && slotIndex >= slotStart) {
                slot = (Slot) this.inventorySlots.get(slotIndex);
                existingStack = slot.getStack();

                if (slot.isItemValid(stack) && existingStack == null) {
                    slot.putStack(stack.copy());
                    slot.onSlotChanged();
                    stack.stackSize = 0;
                    successful = true;
                    break;
                }

                if (reverse) {
                    --slotIndex;
                } else {
                    ++slotIndex;
                }
            }
        }

        return successful;
    }
}
