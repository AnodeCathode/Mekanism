package mekanism.generators.common.tile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import mekanism.api.MekanismConfig.generators;
import mekanism.common.util.ChargeUtils;
import mekanism.common.util.MekanismUtils;
import micdoodle8.mods.galacticraft.api.world.ISolarLevel;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenDesert;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.EnumSet;

import com.bioxx.tfc.Core.WeatherManager;

public class TileEntitySolarGenerator extends TileEntityGenerator
{
	/** Whether or not this generator sees the sun. */
	public boolean seesSun = false;

	/** How fast this tile entity generates energy. */
	public double GENERATION_RATE;

	public TileEntitySolarGenerator()
	{
		this("SolarGenerator", 96000, generators.solarGeneration*2);
		GENERATION_RATE = generators.solarGeneration;
	}

	public TileEntitySolarGenerator(String name, double maxEnergy, double output)
	{
		super("solar", name, maxEnergy, output);
		inventory = new ItemStack[1];
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side)
	{
		return new int[] {0};
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getVolume()
	{
		return 0.05F;
	}

	@Override
	public boolean canSetFacing(int facing)
	{
		return facing != 0 && facing != 1;
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();

		if(!worldObj.isRemote)
		{
			ChargeUtils.charge(0, this);
			
			if(worldObj.isDaytime() && !WeatherManager.isRainingOnCoord(worldObj, xCoord, yCoord, zCoord) && !worldObj.provider.hasNoSky && worldObj.canBlockSeeTheSky(xCoord, yCoord+1, zCoord))
			{
				seesSun = true;
			}
			else {
				seesSun = false;
			}

			if(canOperate())
			{
				setActive(true);
				setEnergy(getEnergy() + getProduction());
			}
			else {
				setActive(false);
			}
		}
	}

	public boolean isDesert()
	{
		return worldObj.provider.getBiomeGenForCoords(xCoord >> 4, zCoord >> 4) instanceof BiomeGenDesert;
	}

	@Override
	public boolean canExtractItem(int slotID, ItemStack itemstack, int side)
	{
		if(slotID == 0)
		{
			return ChargeUtils.canBeOutputted(itemstack, true);
		}

		return false;
	}

	@Override
	public boolean isItemValidForSlot(int slotID, ItemStack itemstack)
	{
		if(slotID == 0)
		{
			return ChargeUtils.canBeCharged(itemstack);
		}

		return true;
	}

	@Override
	public boolean canOperate()
	{
		return getEnergy() < getMaxEnergy() && seesSun && MekanismUtils.canFunction(this);
	}

	public double getProduction()
	{
		double ret = 0;

		if(seesSun)
		{
			ret = GENERATION_RATE;

			if(MekanismUtils.existsAndInstance(worldObj.provider, "micdoodle8.mods.galacticraft.api.world.ISolarLevel"))
			{
				ret *= ((ISolarLevel)worldObj.provider).getSolarEnergyMultiplier();
			}

			if(isDesert())
			{
				ret *= 1.5;
			}

			return ret;
		}

		return 0;
	}

    private static final String[] methods = new String[] {"getStored", "getOutput", "getMaxEnergy", "getEnergyNeeded", "getSeesSun"};

	@Override
	public String[] getMethods()
	{
		return methods;
	}

	@Override
	public Object[] invoke(int method, Object[] arguments) throws Exception
	{
		switch(method)
		{
			case 0:
				return new Object[] {electricityStored};
			case 1:
				return new Object[] {output};
			case 2:
				return new Object[] {BASE_MAX_ENERGY};
			case 3:
				return new Object[] {(BASE_MAX_ENERGY -electricityStored)};
			case 4:
				return new Object[] {seesSun};
			default:
				throw new NoSuchMethodException();
		}
	}

	@Override
	public void handlePacketData(ByteBuf dataStream)
	{
		super.handlePacketData(dataStream);
		seesSun = dataStream.readBoolean();
	}

	@Override
	public ArrayList getNetworkedData(ArrayList data)
	{
		super.getNetworkedData(data);
		data.add(seesSun);
		return data;
	}

	@Override
	public EnumSet<ForgeDirection> getOutputtingSides()
	{
		return EnumSet.of(ForgeDirection.getOrientation(0));
	}

	@Override
	public boolean renderUpdate()
	{
		return false;
	}

	@Override
	public boolean lightUpdate()
	{
		return false;
	}
}
