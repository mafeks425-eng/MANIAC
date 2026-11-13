package com.example.dbdgen.init;

import com.example.dbdgen.DbdGen;
import com.example.dbdgen.tile.GeneratorTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntities {
    public static final DeferredRegister<TileEntityType<?>> TILES =
            DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, DbdGen.MODID);

    public static final RegistryObject<TileEntityType<GeneratorTileEntity>> GENERATOR =
            TILES.register("generator", () -> TileEntityType.Builder
                    .of(GeneratorTileEntity::new, ModBlocks.GENERATOR.get()).build(null));
}
