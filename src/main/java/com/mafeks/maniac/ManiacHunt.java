package com.mafeks.maniac;
import com.mafeks.maniac.registry.ModBlocks;
import com.mafeks.maniac.registry.ModItems;
import com.mafeks.maniac.world.TrapBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
 public static final String MODID="maniachunt";
 public ManiacHunt(){
  var bus=FMLJavaModLoadingContext.get().getModEventBus();
  ModItems.ITEMS.register(bus);
  ModBlocks.BLOCKS.register(bus);
  bus.addListener(this::addCreative);
  MinecraftForge.EVENT_BUS.register(this);
 }
 private void addCreative(BuildCreativeModeTabContentsEvent e){
  // Creative tab entries must be single-count ItemStacks in Minecraft 1.20.1.
  if(e.getTabKey()==net.minecraft.world.item.CreativeModeTabs.TOOLS_AND_UTILITIES){
   ModItems.ITEMS.getEntries().forEach(x->e.accept(new ItemStack(x.get(), 1)));
  }
  if(e.getTabKey()==net.minecraft.world.item.CreativeModeTabs.FUNCTIONAL_BLOCKS){
   e.accept(new ItemStack(ModBlocks.GENERATOR.get(), 1));
   e.accept(new ItemStack(ModBlocks.BEAR_TRAP.get(), 1));
   e.accept(new ItemStack(ModBlocks.TRIPWIRE_TRAP.get(), 1));
   e.accept(new ItemStack(ModBlocks.ROPE_TRAP.get(), 1));
  }
 }
 @SubscribeEvent public void commands(RegisterCommandsEvent e){
  e.getDispatcher().register(Commands.literal("maniac")
   .then(Commands.literal("start").requires(s->s.hasPermission(2))
    .then(Commands.argument("player",net.minecraft.commands.arguments.EntityArgument.player()).executes(c->{
     ServerPlayer p=net.minecraft.commands.arguments.EntityArgument.getPlayer(c,"player");
     p.getPersistentData().putBoolean("maniac_hunt_role",true);
     p.sendSystemMessage(Component.literal("§cТы — Маньяк!"));
     c.getSource().sendSuccess(()->Component.literal("Маньяк выбран: "+p.getGameProfile().getName()),true);
     return 1;
    })))
   .then(Commands.literal("stop").requires(s->s.hasPermission(2)).executes(c->{
    for(ServerPlayer p:c.getSource().getServer().getPlayerList().getPlayers()){
     p.getPersistentData().remove("maniac_hunt_role");
     p.getPersistentData().remove("maniac_hunt_trapped_until");
    }
    c.getSource().sendSuccess(()->Component.literal("Раунд остановлен."),true);
    return 1;
   })));
 }
 @SubscribeEvent public void rescue(PlayerInteractEvent.RightClickBlock e){
  if(e.getLevel().isClientSide||!(e.getLevel().getBlockState(e.getPos()).getBlock() instanceof TrapBlock))return;
  Player rescuer=e.getEntity();
  if(rescuer.getPersistentData().getBoolean("maniac_hunt_role"))return;
  for(Player target:e.getLevel().players())if(target!=rescuer&&target.distanceToSqr(e.getPos().getX()+.5,e.getPos().getY()+.5,e.getPos().getZ()+.5)<=16&&target.getPersistentData().getLong("maniac_hunt_trapped_until")>e.getLevel().getGameTime()){
   target.getPersistentData().remove("maniac_hunt_trapped_until");
   target.removeEffect(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN);
   target.removeEffect(net.minecraft.world.effect.MobEffects.JUMP);
   rescuer.sendSystemMessage(Component.literal("§aТы освободил игрока!"));
   target.sendSystemMessage(Component.literal("§aТебя освободили!"));
   e.setCanceled(true);
   return;
  }
 }
 @SubscribeEvent public void tick(TickEvent.PlayerTickEvent e){
  if(e.phase!=TickEvent.Phase.END||e.player.level().isClientSide)return;
  Player p=e.player;
  long until=p.getPersistentData().getLong("maniac_hunt_trapped_until");
  if(until>p.level().getGameTime()){
   p.setDeltaMovement(0,p.getDeltaMovement().y,0);
   p.hurtMarked=true;
   p.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.MOVEMENT_SLOWDOWN,5,10,false,false));
   p.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.JUMP,5,128,false,false));
  }else if(until!=0)p.getPersistentData().remove("maniac_hunt_trapped_until");
 }
}
