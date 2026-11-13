package com.example.dbdgen.init;

import com.example.dbdgen.DbdGen;
import com.example.dbdgen.block.GeneratorBlock;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, DbdGen.MODID);

    public static final RegistryObject<Block> GENERATOR =
            BLOCKS.register("generator", GeneratorBlock::new);
}
