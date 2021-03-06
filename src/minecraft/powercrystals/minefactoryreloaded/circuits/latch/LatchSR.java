package powercrystals.minefactoryreloaded.circuits.latch;

import net.minecraft.nbt.NBTTagCompound;
import powercrystals.minefactoryreloaded.api.rednet.IRedNetLogicCircuit;

public class LatchSR implements IRedNetLogicCircuit {
    private boolean _value;

    @Override
    public int getInputCount() {
        return 2;
    }

    @Override
    public int getOutputCount() {
        return 2;
    }

    @Override
    public int[] recalculateOutputValues(long worldTime, int[] inputValues) {
        if (inputValues[0] > 0 && inputValues[1] == 0) {
            _value = false;
        } else if (inputValues[0] == 0 && inputValues[1] > 0) {
            _value = true;
        }

        if (_value) {
            return new int[]{15, 0};
        } else {
            return new int[]{0, 15};
        }
    }

    @Override
    public String getUnlocalizedName() {
        return "circuit.mfr.latch.sr";
    }

    @Override
    public String getInputPinLabel(int pin) {
        return pin == 0 ? "R" : "S";
    }

    @Override
    public String getOutputPinLabel(int pin) {
        return pin == 0 ? "Q" : "Q#";
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        _value = tag.getBoolean("state");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setBoolean("state", _value);
    }
}
