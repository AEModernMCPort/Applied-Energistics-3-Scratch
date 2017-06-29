package appeng.core.skyfall;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.bootstrap.InitializationComponentsHandler;
import appeng.api.config.ConfigurationLoader;
import appeng.api.definitions.IDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.api.module.AEStateEvent;
import appeng.api.module.Module;
import appeng.api.module.Module.ModuleEventHandler;
import appeng.core.AppEng;
import appeng.core.lib.bootstrap.InitializationComponentsHandlerImpl;
import appeng.core.skyfall.api.ISkyfall;
import appeng.core.skyfall.api.generator.SkyobjectGenerator;
import appeng.core.skyfall.bootstrap.SkyobjectGeneratorDefinitionBuilder;
import appeng.core.skyfall.config.SkyfallConfig;
import appeng.core.skyfall.definitions.SkyfallSkyobjectGeneratorDefinitions;
import appeng.core.skyfall.proxy.SkyfallProxy;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Module(ISkyfall.NAME)
public class AppEngSkyfall implements ISkyfall {

	public static final Logger logger = LogManager.getLogger(AppEng.NAME + "|"+ NAME);

	@Module.Instance
	public static final AppEngSkyfall INSTANCE = null;

	@SidedProxy(modId = AppEng.MODID, clientSide = "appeng.core.skyfall.proxy.SkyfallClientProxy", serverSide = "appeng.core.skyfall.proxy.SkyfallServerProxy")
	public static SkyfallProxy proxy;

	public SkyfallConfig config;

	private InitializationComponentsHandler initHandler = new InitializationComponentsHandlerImpl();

	private IForgeRegistry<SkyobjectGenerator> skyobjectGeneratorsRegistry;

	private DefinitionFactory registry;

	private SkyfallSkyobjectGeneratorDefinitions skyobjectGeneratorDefinitions;

	private ConfigurationLoader<SkyfallConfig> configLoader;

	@Override
	public <T, D extends IDefinitions<T, ? extends IDefinition<T>>> D definitions(Class<T> clas){
		if(clas == SkyobjectGenerator.class) return (D) skyobjectGeneratorDefinitions;
		return null;
	}

	@ModuleEventHandler
	public void bootstrap(AEStateEvent.AEBootstrapEvent event){
		event.registerDefinitionBuilderSupplier(SkyobjectGenerator.class, SkyobjectGenerator.class, (factory, registryName, skyobjectGenerator) -> new SkyobjectGeneratorDefinitionBuilder<>(factory,registryName, skyobjectGenerator));
	}

	@ModuleEventHandler
	public void preInit(AEStateEvent.AEPreInitializationEvent event){
		skyobjectGeneratorsRegistry = new RegistryBuilder<SkyobjectGenerator>().setName(new ResourceLocation(AppEng.MODID, "skyobject_generator")).setType(SkyobjectGenerator.class).disableSaving().setMaxID(Integer.MAX_VALUE - 1).create();

		configLoader = event.configurationLoader();
		try{
			configLoader.load(SkyfallConfig.class);
		} catch(IOException e){
			logger.error("Caught exception loading configuration", e);
		}
		config = configLoader.configuration();

		registry = event.factory(initHandler, proxy);
		skyobjectGeneratorDefinitions = new SkyfallSkyobjectGeneratorDefinitions(registry);

		skyobjectGeneratorDefinitions.init(registry);

		initHandler.preInit();
		proxy.preInit(event);
	}

	@ModuleEventHandler
	public void init(AEStateEvent.AEInitializationEvent event){
		initHandler.init();
		proxy.init(event);
	}

	@ModuleEventHandler
	public void postInit(AEStateEvent.AEPostInitializationEvent event){
		initHandler.postInit();
		proxy.postInit(event);

		config.initPostLoad(skyobjectGeneratorsRegistry);
		try{
			configLoader.save();
		} catch(IOException e){
			logger.error("Caught exception saving configuration", e);
		}
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
