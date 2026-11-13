package com.example.dbdgen.init;

import com.example.dbdgen.DbdGen;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, DbdGen.MODID);

    public static final RegistryObject<Item> GENERATOR = ITEMS.register("generator",
            () -> new BlockItem(ModBlocks.GENERATOR.get(), new Item.Properties().tab(ItemGroup.TAB_DECORATIONS)));

    // Гаечный ключ: чинит генератор в 3 раза быстрее, чем руки
    public static final RegistryObject<Item> WRENCH = ITEMS.register("wrench",
            () -> new Item(new Item.Properties().tab(ItemGroup.TAB_TOOLS).stacksTo(1)));

    // Канистра: 4 заправки
    public static final RegistryObject<Item> FUEL_CAN = ITEMS.register("fuel_can",
            () -> new Item(new Item.Properties().tab(ItemGroup.TAB_MISC).durability(4)));
}
