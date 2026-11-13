package com.example.dbdgen.tile;

import com.example.dbdgen.block.GeneratorBlock;
import com.example.dbdgen.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

public class GeneratorTileEntity extends TileEntity implements ITickableTileEntity {

    // ===== НАСТРОЙКИ (меняй под себя) =====
    public static final int MAX_FUEL = 6000;            // тиков работы (6000 = 5 минут)
    public static final int FUEL_PER_CAN_USE = 1500;    // сколько топлива даёт одна заправка
    public static final int MAX_REPAIR = 100;           // 100% = починен
    public static final int HAND_REPAIR = 1;            // прогресс за одно нажатие руками
    public static final int WRENCH_REPAIR = 3;          // прогресс за нажатие с гаечным ключом
    public static final float BREAK_CHANCE = 1.0F / 2400.0F; // шанс поломки за тик (~раз в 2 мин)
    // ======================================

    private int fuel = 0;
    private int repair = 0; // новый генератор сломан

    public GeneratorTileEntity() {
        super(ModTileEntities.GENERATOR.get());
    }

    public int getFuel() { return fuel; }
    public int getRepair() { return repair; }
    public boolean isRepaired() { return repair >= MAX_REPAIR; }

    public void addFuel(int amount) {
        fuel = Math.min(MAX_FUEL, fuel + amount);
        setChanged();
        updateState();
    }

    public void addRepair(int amount) {
        repair = Math.min(MAX_REPAIR, repair + amount);
        setChanged();
        updateState();
    }

    @Override
    public void tick() {
        if (level == null || level.isClientSide) return;

        if (isRepaired() && fuel > 0) {
            fuel--;
            if (level.random.nextFloat() < BREAK_CHANCE) {
                repair = 0; // поломка!
                level.playSound(null, worldPosition, SoundEvents.ANVIL_LAND, SoundCategory.BLOCKS, 0.6F, 0.5F);
            }
            setChanged();
        }
        updateState();
    }

    private void updateState() {
        if (level == null || level.isClientSide) return;
        BlockState state = getBlockState();
        boolean repaired = isRepaired();
        boolean lit = repaired && fuel > 0;
        if (state.getValue(GeneratorBlock.LIT) != lit || state.getValue(GeneratorBlock.REPAIRED) != repaired) {
            level.setBlock(worldPosition,
                    state.setValue(GeneratorBlock.LIT, lit).setValue(GeneratorBlock.REPAIRED, repaired), 3);
        }
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        fuel = nbt.getInt("Fuel");
        repair = nbt.getInt("Repair");
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.putInt("Fuel", fuel);
        nbt.putInt("Repair", repair);
        return nbt;
    }
}
