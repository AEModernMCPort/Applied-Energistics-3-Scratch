package appeng.core.core;

import appeng.api.definitions.IDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.api.module.AEStateEvent;
import appeng.api.module.Module;
import appeng.api.module.Module.ModuleEventHandler;
import appeng.core.AppEng;
import appeng.core.api.ICore;
import appeng.core.api.material.Material;
import appeng.core.core.bootstrap.*;
import appeng.core.core.proxy.CoreProxy;
import appeng.core.core.definitions.*;
import appeng.core.lib.bootstrap_olde.FeatureFactory;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.RegistryBuilder;

@Module(value = ICore.NAME, dependencies = "hard-before:module-*")
public class AppEngCore implements ICore {

	@Module.Instance(NAME)
	public static final AppEngCore INSTANCE = null;

	@SidedProxy(modId = AppEng.MODID, clientSide = "appeng.core.core.proxy.CoreClientProxy", serverSide = "appeng.core.core.proxy.CoreServerProxy")
	public static CoreProxy proxy;

	private FMLControlledNamespacedRegistry<Material> materialRegistry;

	private FeatureFactory registry;

	private CoreItemDefinitions itemDefinitions;
	private CoreBlockDefinitions blockDefinitions;
	private CoreTileDefinitions tileDefinitions;
	private CoreMaterialDefinitions materialDefinitions;
	private CoreEntityDefinitions entityDefinitions;

	public AppEngCore(){

	}

	@Override
	public <T, D extends IDefinitions<T, ? extends IDefinition<T>>> D definitions(Class<T> clas){
		if(clas == Item.class){
			return (D) itemDefinitions;
		}
		if(clas == Block.class){
			return (D) blockDefinitions;
		}
		if(clas == TileEntity.class){
			return (D) tileDefinitions;
		}
		if(clas == Material.class){
			return (D) materialDefinitions;
		}
		if(clas == EntityEntry.class){
			return (D) entityDefinitions;
		}
		return null;
	}

	public FMLControlledNamespacedRegistry<Material> getMaterialRegistry(){
		return materialRegistry;
	}

	@ModuleEventHandler
	public void bootstrap(AEStateEvent.AEBootstrapEvent event){
		event.registerDefinitionBuilderSupplier(Item.class, Item.class, (factory, registryName, item) -> new ItemDefinitionBuilder(factory, registryName, item));
		event.registerDefinitionBuilderSupplier(Block.class, Block.class, (factory, registryName, block) -> new BlockDefinitionBuilder(factory, registryName, block));
		//TODO 1.11.2-ReOver - Find something better than Class for tiles & fix NPE
		event.registerDefinitionBuilderSupplier(Class.class, Class.class, (factory, registryName, tile) -> new TileDefinitionBuilder(factory, registryName, tile, null));
		event.registerDefinitionBuilderSupplier(Biome.class, Biome.class, (factory, registryName, biome) -> new BiomeDefinitionBuilder(factory, registryName, biome));
		event.registerDefinitionBuilderSupplier(DimensionType.class, Integer.class, (factory, registryName, dimensionId) -> new DimensionTypeDefinitionBuilder(factory, registryName, dimensionId));

		event.registerDefinitionBuilderSupplier(Material.class, Material.class, (factory, registryName, material) -> new MaterialDefinitionBuilder(factory, registryName, material));
	}

	@ModuleEventHandler
	public void preInit(AEStateEvent.AEPreInitializationEvent event){
		materialRegistry = (FMLControlledNamespacedRegistry<Material>) new RegistryBuilder().setName(new ResourceLocation(AppEng.MODID, "material")).setType(Material.class).setIDRange(0, Short.MAX_VALUE).create();

		registry = new FeatureFactory();
		this.materialDefinitions = new CoreMaterialDefinitions(registry);
		this.blockDefinitions = new CoreBlockDefinitions(registry);
		this.itemDefinitions = new CoreItemDefinitions(registry);
		this.tileDefinitions = new CoreTileDefinitions(registry);
		this.entityDefinitions = new CoreEntityDefinitions(registry);
		registry.preInit(event);

		proxy.preInit(event);
	}

	@ModuleEventHandler
	public void init(AEStateEvent.AEInitializationEvent event){
		registry.init(event);
		proxy.init(event);
	}

	@ModuleEventHandler
	public void postInit(AEStateEvent.AEPostInitializationEvent event){
		registry.postInit(event);
		proxy.postInit(event);
	}

	@ModuleEventHandler
	public void handleIMCEvent(AEStateEvent.ModuleIMCMessageEvent event){

	}

	/*@ModuleEventHandler
	public void serverAboutToStart(FMLServerAboutToStartEvent event){

	}

	@ModuleEventHandler
	public void serverStarting(FMLServerStartingEvent event){

	}

	@ModuleEventHandler
	public void serverStopping(FMLServerStoppingEvent event){

	}

	@ModuleEventHandler
	public void serverStopped(FMLServerStoppedEvent event){

	}*/

}
