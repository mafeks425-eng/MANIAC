package com.mafeks.maniac;

import com.mafeks.maniac.registry.ModBlocks;
import com.mafeks.maniac.registry.ModItems;
import com.mafeks.maniac.world.TrapBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.commands.Commands;

@Mod(ManiacHunt.MODID)
public class ManiacHunt {
    public static final String MODID = "maniac hunt".replace(" ", "");
    public ManiacHunt() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(bus);
        ModBlocks.BLOCKS.register(bus);
        bus.addListener(this::addCreative);
        MinecraftForge.EVENT_BUS.register(this);
    }
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            ModItems.ITEMS.getEntries().forEach(e -> event.accept(e.get()));
        }
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModBlocks.GENERATOR.get());
            event.accept(ModBlocks.BEAR_TRAP.get());
            event.accept(ModBlocks.TRIPWIRE_TRAP.get());
            event.accept(ModBlocks.ROPE_TRAP.get());
        }
    }
    @SubscribeEvent
    public void commands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("maniac")
            .then(Commands.literal("start").requires(s -> s.hasPermission(2))
                .then(Commands.argument("player", net.minecraft.commands.arguments.EntityArgument.player())
                    .executes(ctx -> {
                        ServerPlayer chosen = net.minecraft.commands.arguments.EntityArgument.getPlayer(ctx, "player");
                        chosen.getPersistentData().putBoolean("maniac_hunt_role", true);
                        chosen.sendSystemMessage(Component.literal("§cТы — Маньяк!"));
                        ctx.getSource().sendSuccess(() -> Component.literal("Маньяк выбран: " + chosen.getGameProfile().getName()), true);
                        return 1;
                    })))
            .then(Commands.literal("stop").requires(s -> s.hasPermission(2)).executes(ctx -> {
                for (ServerPlayer p : ctx.getSource().getServer().getPlayerList().getPlayers()) {
                    p.getPersistentData().remove("maniac_hunt_role");
                    p.getPersistentData().remove("maniac_hunt_trapped_until");
                }
                ctx.getSource().sendSuccess(() -> Component.literal("Раунд Maniac Hunt остановлен."), true);
                return 1;
            })));
    }
    @SubscribeEvent
    public void onTrapInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide || event.getEntity().getMainHandItem().isEmpty()) return;
        BlockPos pos = event.getPos();
        Block block = event.getLevel().getBlockState(pos).getBlock();
        if (!(block instanceof TrapBlock)) return;
        Player rescuer = event.getEntity();
        if (rescuer.getPersistentData().getBoolean("maniac_hunt_role")) return;
        for (Player target : event.getLevel().players()) {
            if (target != rescuer && target.distanceToSqr(pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5) <= 16
                && target.getPersistentData().getLong("maniac_hunt_trapped_until") > event.getLevel().getGameTime()) {
                target.getPersistentData().remove("maniac_hunt_trapped_until");
                target.removeEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN);
                target.removeEffect(net.minecraft.world.effect.MobEffects.JUMP);
                rescuer.sendSystemMessage(Component.literal("§aТы освободил игрока!"));
                target.sendSystemMessage(Component.literal("§aТебя освободили!"));
                event.setCanceled(true);
                return;
            }
        }
    }
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;
        Player p = event.player;
        long until = p.getPersistentData().getLong("maniac_hunt_trapped_until");
        if (until > p.level().getGameTime()) {
            p.setDeltaMovement(0, p.getDeltaMovement().y, 0);
            p.hurtMarked = true;
            p.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN, 5, 10, false, false));
            p.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.JUMP, 5, 128, false, false));
        } else if (until != 0) {
            p.getPersistentData().remove("maniac_hunt_trapped_until");
        }
    }
}
