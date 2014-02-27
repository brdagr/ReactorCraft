/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ReactorCraft.Items;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.ReactorCraft.ReactorCraft;
import Reika.ReactorCraft.Base.ReactorItemBase;
import Reika.ReactorCraft.Registry.ReactorItems;
import Reika.ReactorCraft.Registry.ReactorTiles;
import Reika.RotaryCraft.API.ChargeableTool;

public class ItemRemoteControl extends ReactorItemBase implements ChargeableTool {

	public ItemRemoteControl(int ID, int tex) {
		super(ID, tex);
		maxStackSize = 1;
		canRepair = false;
		hasSubtypes = false;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		if (this.canUse(is, world, ep)) {
			TileEntity te = this.getLinkedCPU(is);
			if (te != null) {
				int x = is.stackTagCompound.getInteger("cx");
				int y = is.stackTagCompound.getInteger("cy");
				int z = is.stackTagCompound.getInteger("cz");
				int dim = is.stackTagCompound.getInteger("id");
				World w = DimensionManager.getWorld(dim);
				int id = w.getBlockId(x, y, z);
				int meta = w.getBlockMetadata(x, y, z);
				if (id == ReactorTiles.CPU.getBlockID() && meta == ReactorTiles.CPU.getBlockMetadata()) {
					ep.openGui(ReactorCraft.instance, 0, w, x, y, z);
					//is.setItemDamage(is.getItemDamage()-1);
				}
			}
		}
		return is;
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		int id = world.getBlockId(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		if (id == ReactorTiles.CPU.getBlockID() && meta == ReactorTiles.CPU.getBlockMetadata()) {
			TileEntity te = world.getBlockTileEntity(x, y, z);
			if (te != null) {
				this.setLinkedCPU(is, te);
				return true;
			}
		}
		return false;
	}

	public TileEntity getLinkedCPU(ItemStack is) {
		if (is.itemID == itemID) {
			if (is.stackTagCompound != null) {
				int x = is.stackTagCompound.getInteger("cx");
				int y = is.stackTagCompound.getInteger("cy");
				int z = is.stackTagCompound.getInteger("cz");
				int dim = is.stackTagCompound.getInteger("id");
				TileEntity te = DimensionManager.getWorld(dim).getBlockTileEntity(x, y, z);
				return te;
			}
		}
		return null;
	}

	public boolean canUse(ItemStack is, World world, EntityPlayer ep) {
		if (is.getItemDamage() > 0 && is.stackTagCompound != null) {
			int x = is.stackTagCompound.getInteger("cx");
			int y = is.stackTagCompound.getInteger("cy");
			int z = is.stackTagCompound.getInteger("cz");
			int dim = is.stackTagCompound.getInteger("id");
			if (dim == world.provider.dimensionId || this.canWorkInterdimensionally(is)) {
				int ex = MathHelper.floor_double(ep.posX);
				int ey = MathHelper.floor_double(ep.posY);
				int ez = MathHelper.floor_double(ep.posZ);
				double dd = ReikaMathLibrary.py3d(ex-x, ey-y, ez-z);
				return DimensionManager.getWorld(dim) != null && this.getRange(is)+0.5 >= dd;
			}
		}
		return false;
	}

	private void setLinkedCPU(ItemStack is, TileEntity te) {
		is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setInteger("cx", te.xCoord);
		is.stackTagCompound.setInteger("cy", te.yCoord);
		is.stackTagCompound.setInteger("cz", te.zCoord);
		is.stackTagCompound.setInteger("id", te.worldObj.provider.dimensionId);
	}

	public boolean canWorkInterdimensionally(ItemStack is) {
		return is.getItemDamage() > 8192;
	}

	public int getRange(ItemStack is) {
		return 4*(int)ReikaMathLibrary.logbase(is.getItemDamage(), 2);
	}

	@Override
	public void getSubItems(int id, CreativeTabs tab, List li) {
		li.add(ReactorItems.REMOTE.getStackOfMetadata(32000));
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean par4) {
		if (is.stackTagCompound != null) {
			int x = is.stackTagCompound.getInteger("cx");
			int y = is.stackTagCompound.getInteger("cy");
			int z = is.stackTagCompound.getInteger("cz");
			int dim = is.stackTagCompound.getInteger("id");
			li.add(String.format("Linked to CPU in world %d at %d, %d, %d", dim, x, y, z));
		}
		else {
			li.add("No linked CPU");
		}
	}
}