package appeng.core.lib.bootstrap_olde.components;

import appeng.api.bootstrap.InitializationComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;

public class ItemColorComponent implements InitializationComponent.Init {

	private final Item item;

	private final IItemColor itemColor;

	public ItemColorComponent(Item item, IItemColor itemColor){
		this.item = item;
		this.itemColor = itemColor;
	}

	@Override
	public void init(Side side){
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(itemColor, item);
	}
}