package com.mafeks.maniac.registry;

import com.mafeks.maniac.ManiacHunt;
import com.mafeks.maniac.world.GeneratorBlock;
import com.mafeks.maniac.world.TrapBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ManiacHunt.MODID);
    public static final RegistryObject<Block> GENERATOR = BLOCKS.register("generator", () -> new GeneratorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.5F).noOcclusion()));
    public static final RegistryObject<Block> BEAR_TRAP = BLOCKS.register("bear_trap", () -> new TrapBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.5F).noOcclusion(), 160));
    public static final RegistryObject<Block> TRIPWIRE_TRAP = BLOCKS.register("tripwire_trap", () -> new TrapBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1.0F).noOcclusion(), 120));
    public static final RegistryObject<Block> ROPE_TRAP = BLOCKS.register("rope_trap", () -> new TrapBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(2.0F).noOcclusion(), 200));
}
