package dex.mapwriter3.forge;

import java.io.File;

import dex.mapwriter3.Mw;
import dex.mapwriter3.api.MwAPI;
import dex.mapwriter3.config.ConfigurationHandler;
import dex.mapwriter3.overlay.OverlayGrid;
import dex.mapwriter3.overlay.OverlaySlime;
import dex.mapwriter3.region.MwChunk;
import dex.mapwriter3.util.MwReference;
import dex.mapwriter3.util.VersionCheck;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class ClientProxy extends CommonProxy
{

	@Override
	public void preInit(File configFile)
	{
		ConfigurationHandler.init(configFile);
		FMLCommonHandler.instance().bus().register(new ConfigurationHandler());
	}

	@Override
	public void load()
	{
		EventHandler eventHandler = new EventHandler(Mw.getInstance());
		MinecraftForge.EVENT_BUS.register(eventHandler);
		FMLCommonHandler.instance().bus().register(eventHandler);

		MwKeyHandler keyEventHandler = new MwKeyHandler();
		FMLCommonHandler.instance().bus().register(keyEventHandler);
		MinecraftForge.EVENT_BUS.register(keyEventHandler);
	}

	@Override
	public void postInit()
	{
		if (Loader.isModLoaded("VersionChecker"))
		{
			FMLInterModComms.sendRuntimeMessage(MwReference.MOD_ID, "VersionChecker", "addVersionCheck", MwReference.VersionURL);
		}
		else
		{
			VersionCheck versionCheck = new VersionCheck();
			Thread versionCheckThread = new Thread(versionCheck, "Version Check");
			versionCheckThread.start();
		}
		if (Loader.isModLoaded("CarpentersBlocks"))
		{
			MwChunk.carpenterdata();
		}
		if (Loader.isModLoaded("ForgeMultipart"))
		{
			MwChunk.FMPdata();

		}
		MwAPI.registerDataProvider("Slime", new OverlaySlime());
		MwAPI.registerDataProvider("Grid", new OverlayGrid());
	}
}
