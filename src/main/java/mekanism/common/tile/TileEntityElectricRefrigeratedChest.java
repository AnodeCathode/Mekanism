package mekanism.common.tile;

import java.util.ArrayList;

import com.bioxx.tfc.Core.TFC_Core;
import com.bioxx.tfc.Items.ItemTerra;
import com.bioxx.tfc.Items.ItemBlocks.ItemTerraBlock;
import com.bioxx.tfc.api.TFC_ItemHeat;

import cpw.mods.fml.relauncher.Side;
import mekanism.common.PacketHandler;
import mekanism.common.security.ISecurityTile;
import mekanism.common.tile.component.TileComponentSecurity;
import mekanism.common.util.ChargeUtils;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.SecurityUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import io.netty.buffer.ByteBuf;

public class TileEntityElectricRefrigeratedChest extends TileEntityElectricBlock implements ISecurityTile
{

    public static int[] INV;

    public float lidAngle;

    public float prevLidAngle;

    public TileComponentSecurity securityComponent = new TileComponentSecurity(this);

    public TileEntityElectricRefrigeratedChest()
    {
        super("ElectricRefrigeratedChest", 12000);
        inventory = new ItemStack[55];
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        handleItemTicking(this, this.worldObj, xCoord, yCoord, zCoord);


        prevLidAngle = lidAngle;
        float increment = 0.1F;

        if((playersUsing.size() > 0) && (lidAngle == 0.0F))
        {
            worldObj.playSoundEffect(xCoord + 0.5F, yCoord + 0.5D, zCoord + 0.5F, "random.chestopen", 0.5F, (worldObj.rand.nextFloat()*0.1F) + 0.9F);
        }

        if((playersUsing.size() == 0 && lidAngle > 0.0F) || (playersUsing.size() > 0 && lidAngle < 1.0F))
        {
            float angle = lidAngle;

            if(playersUsing.size() > 0)
            {
                lidAngle += increment;
            }
            else {
                lidAngle -= increment;
            }

            if(lidAngle > 1.0F)
            {
                lidAngle = 1.0F;
            }

            float split = 0.5F;

            if(lidAngle < split && angle >= split)
            {
                worldObj.playSoundEffect(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D, "random.chestclosed", 0.5F, (worldObj.rand.nextFloat()*0.1F) + 0.9F);
            }

            if(lidAngle < 0.0F)
            {
                lidAngle = 0.0F;
            }
        }

        ChargeUtils.discharge(54, this);
    }

    private void handleItemTicking(TileEntityElectricRefrigeratedChest te, World world, int x, int y, int z) {
        //If the chest is actively receiving power it should decay extremely slowly, otherwise normal chest decay.
        IInventory iinv = (TileEntityElectricRefrigeratedChest)te;

        boolean isPowered = te.getEnergy() > 0;
        for (int i = 0; !world.isRemote && i < iinv.getSizeInventory(); i++)
        {
            ItemStack is = iinv.getStackInSlot(i);
            if (is != null && iinv.getStackInSlot(i).stackSize <= 0)
                iinv.setInventorySlotContents(i, null);

            if (is != null)
            {
                if(is.stackSize == 0)
                {
                    iinv.setInventorySlotContents(i, null);
                    continue;
                }
                // right here we need to test if the chest is actively receiving power and override the normal decay process.
                if (!isPowered){
                    if (is.getItem() instanceof ItemTerra && ((ItemTerra) is.getItem()).onUpdate(is, world, x, y, z))
                        continue;
                    else if (is.getItem() instanceof ItemTerraBlock && ((ItemTerraBlock) is.getItem()).onUpdate(is, world, x, y, z))
                        continue;
                    is = TFC_Core.tickDecay(is, world, x, y, z, 2, 1f);
                    if(is != null)
                        TFC_ItemHeat.handleItemHeat(is);
                    iinv.setInventorySlotContents(i, is);

                }
                else {
                    is = TFC_Core.tickDecay(is, world, x, y, z, 1, .02f);
                    te.setEnergy(te.getEnergy() - 25);
                    if(is != null)
                    {
                        if(is.hasTagCompound())
                        {
                            NBTTagCompound comp = is.getTagCompound();
                            if(is.hasTagCompound() && is.getTagCompound().hasKey("temperature"))
                            {
                                float temp = is.getTagCompound().getFloat("temperature");
                                if(temp > 0)
                                {
                                    temp = temp - 10f ;
                                    comp.setFloat("temperature",temp);
                                    te.setEnergy(te.getEnergy() - 10);
                                }
                                if(temp <= 0)
                                    comp.removeTag("temperature");
                                if(comp.hasNoTags())
                                    is.stackTagCompound = null;
                            }
                        }
                        iinv.setInventorySlotContents(i, is);
                    }



                }
            }
        }

    }

    @Override
    public boolean isItemValidForSlot(int slotID, ItemStack itemstack)
    {
        if(slotID == 54)
        {
            return ChargeUtils.canBeDischarged(itemstack);
        }
        else {
            return true;
        }
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        if(side == 0 || SecurityUtils.getSecurity(this, Side.SERVER) != SecurityMode.PUBLIC && getEnergy() == 0)
        {
            return InventoryUtils.EMPTY;
        }
        else {
            if(INV == null)
            {
                INV = new int[55];

                for(int i = 0; i < INV.length; i++)
                {
                    INV[i] = i;
                }
            }

            return INV;
        }
    }


    @Override
    public boolean canExtractItem(int slotID, ItemStack itemstack, int side)
    {
        if(slotID == 54)
        {
            return ChargeUtils.canBeOutputted(itemstack, false);
        }
        else {
            return true;
        }
    }

    @Override
    public boolean wrenchCanRemove(EntityPlayer entityPlayer)
    {
        return SecurityUtils.canAccess(entityPlayer, this);
    }


    @Override
    public boolean canSetFacing(int side)
    {
        return side != 0 && side != 1;
    }

    @Override
    public TileComponentSecurity getSecurity()
    {
        return securityComponent;
    }
}
