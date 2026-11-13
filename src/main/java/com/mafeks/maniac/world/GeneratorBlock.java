package com.mafeks.maniac.world;

import com.mafeks.maniac.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class GeneratorBlock extends Block {
    public GeneratorBlock(Properties properties) { super(properties); }
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!level.getBlockTicks().hasScheduledTick(pos, this)) {
            if (player.getItemInHand(hand).is(ModItems.FUEL.get())) {
                player.getItemInHand(hand).shrink(1);
                level.scheduleTick(pos, this, 20 * 5);
                player.sendSystemMessage(Component.literal("§eГенератор заправлен. Теперь нужен ремонт гаечным ключом (ПКМ)."));
            } else if (player.getItemInHand(hand).is(ModItems.WRENCH.get())) {
                level.setBlock(pos, state.setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT, true), 3);
                player.sendSystemMessage(Component.literal("§aГенератор запущен! Выход разблокирован."));
            } else {
                player.sendSystemMessage(Component.literal("§7Нужна канистра топлива, затем гаечный ключ."));
            }
        } else {
            player.sendSystemMessage(Component.literal("§eГенератор ещё заправляется..."));
        }
        return InteractionResult.CONSUME;
    }
    @Override
    public void tick(BlockState state, net.minecraft.server.level.ServerLevel level, BlockPos pos, java.util.Random random) {
        level.setBlock(pos, state, 3);
    }
}
