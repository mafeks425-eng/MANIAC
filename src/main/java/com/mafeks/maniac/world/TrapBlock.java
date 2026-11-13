package com.mafeks.maniac.world;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class TrapBlock extends Block {
    private final int duration;
    public TrapBlock(Properties properties, int duration) { super(properties); this.duration = duration; }
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level.isClientSide || !(entity instanceof Player player) || player.getPersistentData().getBoolean("maniac_hunt_role")) return;
        long until = player.getPersistentData().getLong("maniac_hunt_trapped_until");
        if (until > level.getGameTime()) return;
        player.getPersistentData().putLong("maniac_hunt_trapped_until", level.getGameTime() + duration);
        player.sendSystemMessage(Component.literal("§cТы попал в ловушку! Другой выживший может освободить тебя ПКМ по ловушке."));
        level.levelEvent(2001, pos, Block.getId(state));
    }
    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        entityInside(state, level, pos, entity);
        super.stepOn(level, pos, state, entity);
    }
}
