package com.example.dbdgen.block;

import com.example.dbdgen.init.ModItems;
import com.example.dbdgen.tile.GeneratorTileEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class GeneratorBlock extends Block {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;          // работает
    public static final BooleanProperty REPAIRED = BooleanProperty.create("repaired"); // починен

    public GeneratorBlock() {
        super(AbstractBlock.Properties.of(Material.METAL)
                .strength(3.5F)
                .sound(SoundType.METAL)
                .lightLevel(s -> s.getValue(LIT) ? 13 : 0));
        registerDefaultState(stateDefinition.any().setValue(LIT, false).setValue(REPAIRED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(LIT, REPAIRED);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new GeneratorTileEntity();
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player,
                                Hand hand, BlockRayTraceResult hit) {
        if (world.isClientSide) return ActionResultType.SUCCESS;

        TileEntity te = world.getBlockEntity(pos);
        if (!(te instanceof GeneratorTileEntity)) return ActionResultType.PASS;
        GeneratorTileEntity gen = (GeneratorTileEntity) te;
        ItemStack stack = player.getItemInHand(hand);

        // ---------- ЗАПРАВКА ----------
        if (stack.getItem() == ModItems.FUEL_CAN.get()) {
            if (player.getCooldowns().isOnCooldown(stack.getItem())) return ActionResultType.CONSUME;
            if (gen.getFuel() >= GeneratorTileEntity.MAX_FUEL) {
                player.displayClientMessage(new TranslationTextComponent("message.dbdgen.full"), true);
                return ActionResultType.CONSUME;
            }
            gen.addFuel(GeneratorTileEntity.FUEL_PER_CAN_USE);
            world.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 0.8F);
            if (!player.isCreative()) {
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
            }
            player.getCooldowns().addCooldown(stack.getItem(), 20);
            int pct = gen.getFuel() * 100 / GeneratorTileEntity.MAX_FUEL;
            player.displayClientMessage(new TranslationTextComponent("message.dbdgen.fueled", pct), true);
            return ActionResultType.CONSUME;
        }

        // ---------- РЕМОНТ (зажми ПКМ) ----------
        if (!gen.isRepaired()) {
            boolean wrench = stack.getItem() == ModItems.WRENCH.get();
            gen.addRepair(wrench ? GeneratorTileEntity.WRENCH_REPAIR : GeneratorTileEntity.HAND_REPAIR);
            float pitch = 0.8F + world.random.nextFloat() * 0.5F;
            world.playSound(null, pos, SoundEvents.CHAIN_HIT, SoundCategory.BLOCKS, 1.0F, wrench ? pitch + 0.3F : pitch);

            if (gen.isRepaired()) {
                world.playSound(null, pos, SoundEvents.ANVIL_USE, SoundCategory.BLOCKS, 0.8F, 1.2F);
                player.displayClientMessage(new TranslationTextComponent("message.dbdgen.repaired"), true);
            } else {
                player.displayClientMessage(new TranslationTextComponent("message.dbdgen.repair", gen.getRepair()), true);
            }
            return ActionResultType.CONSUME;
        }

        // ---------- СТАТУС ----------
        int fuelPct = gen.getFuel() * 100 / GeneratorTileEntity.MAX_FUEL;
        player.displayClientMessage(new TranslationTextComponent("message.dbdgen.status", fuelPct), true);
        return ActionResultType.CONSUME;
    }

    // Работающий генератор выдаёт сигнал редстоуна
    @Override
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public int getSignal(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        return state.getValue(LIT) ? 15 : 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
        double x = pos.getX() + 0.2 + rand.nextDouble() * 0.6;
        double z = pos.getZ() + 0.2 + rand.nextDouble() * 0.6;
        double y = pos.getY() + 1.0;
        if (state.getValue(LIT)) {
            if (rand.nextInt(4) == 0) world.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0.04, 0);
        } else if (!state.getValue(REPAIRED) && rand.nextInt(6) == 0) {
            world.addParticle(ParticleTypes.LARGE_SMOKE, x, y, z, 0, 0.03, 0);
            world.addParticle(ParticleTypes.CRIT, x, y - 0.2, z, 0, 0.1, 0);
        }
    }
}
