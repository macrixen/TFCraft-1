package TFC.Handlers;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.ForgeSubscribe;
import TFC.TFCItems;
import TFC.API.Events.AnvilCraftEvent;
import TFC.API.Events.ItemMeltEvent;

public class AnvilCraftingHandler
{
	@ForgeSubscribe
	public void onAnvilCraft(AnvilCraftEvent event) 
	{
		if(event.input1.itemID == TFCItems.Bloom.itemID && event.input1.getItemDamage() > 100)
		{
			int dam = event.input1.getItemDamage();
			ItemStack out1 = new ItemStack(TFCItems.Bloom, dam/100, 100);
			ItemStack out2 = new ItemStack(TFCItems.Bloom, 1, dam-(dam/100*100));
			if(!((EntityPlayer)event.entity).inventory.addItemStackToInventory(out1))
			{
				event.entity.worldObj.spawnEntityInWorld(new EntityItem(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ, out1));
			}

			if(out2.getItemDamage() > 0 && !((EntityPlayer)event.entity).inventory.addItemStackToInventory(out2))
			{
				event.entity.worldObj.spawnEntityInWorld(new EntityItem(event.entity.worldObj, event.entity.posX, event.entity.posY, event.entity.posZ, out2));
			}
			event.result = null;
		}
	}

	@ForgeSubscribe
	public void onItemMelt(ItemMeltEvent event) 
	{
		if(event.input1 != null)
		{
			if((event.input1.itemID == TFCItems.Bloom.itemID || event.input1.itemID == TFCItems.RawBloom.itemID) && event.result.getItemDamage() > 100)
			{
				event.result = event.input1;
				event.result.setItemDamage(event.result.getItemDamage()-1);
			}
			else if((event.input1.itemID == TFCItems.Bloom.itemID || event.input1.itemID == TFCItems.RawBloom.itemID) && event.result.getItemDamage() <= 100)
			{
				event.result.setItemDamage(100-event.input1.getItemDamage());
			}
		}
	}
}
