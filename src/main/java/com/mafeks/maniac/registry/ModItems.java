package com.mafeks.maniac.registry;

import com.mafeks.maniac.ManiacHunt;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ManiacHunt.MODID);
    public static final RegistryObject<Item> BAT = item("bat", 1);
    public static final RegistryObject<Item> CROWBAR = item("crowbar", 1);
    public static final RegistryObject<Item> SCISSORS = item("scissors", 1);
    public static final RegistryObject<Item> WRENCH = item("wrench", 1);
    public static final RegistryObject<Item> FUEL = item("fuel_can", 16);
    public static final RegistryObject<Item> KEYCARD = item("keycard", 1);
    private static RegistryObject<Item> item(String name, int stack) {
        return ITEMS.register(name, () -> new Item(new Item.Properties().stacksTo(stack)));
    }
}
