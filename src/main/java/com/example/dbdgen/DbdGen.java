package com.example.dbdgen;

import com.example.dbdgen.init.ModBlocks;
import com.example.dbdgen.init.ModItems;
import com.example.dbdgen.init.ModTileEntities;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DbdGen.MODID)
public class DbdGen {
    public static final String MODID = "dbdgen";

    public DbdGen() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(bus);
        ModTileEntities.TILES.register(bus);
        ModItems.ITEMS.register(bus);
    }
}
