package mekanism.common.inventory.slot;

import mekanism.common.block.BlockMachine.MachineType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotElectricRefrigeratedChest extends Slot
{
    public SlotElectricRefrigeratedChest(IInventory inventory, int index, int x, int y)
    {
        super(inventory, index, x, y);
    }

    @Override
    public boolean canTakeStack(EntityPlayer player)
    {
        if(inventory.getStackInSlot(getSlotIndex()) == null)
        {
            return false;
        }

        return MachineType.get(inventory.getStackInSlot(getSlotIndex())) != MachineType.ELECTRIC_REFRIGERATED_CHEST;
    }
}