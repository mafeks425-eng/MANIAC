package com.mafeks.maniac.world;
import com.mafeks.maniac.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
public class GeneratorBlock extends Block {
 public static final BooleanProperty FUELED=BooleanProperty.create("fueled"), RUNNING=BooleanProperty.create("running");
 public GeneratorBlock(BlockBehaviour.Properties p){super(p);registerDefaultState(stateDefinition.any().setValue(FUELED,false).setValue(RUNNING,false));}
 @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> b){b.add(FUELED,RUNNING);}
 @Override public InteractionResult use(BlockState s,Level l,BlockPos p,Player player,InteractionHand h,BlockHitResult hit){
  if(l.isClientSide)return InteractionResult.SUCCESS;
  var held=player.getItemInHand(h);
  if(!s.getValue(FUELED)&&held.is(ModItems.FUEL.get())){if(!player.getAbilities().instabuild)held.shrink(1);l.setBlock(p,s.setValue(FUELED,true),3);player.sendSystemMessage(Component.literal("§eГенератор заправлен. Теперь почини его гаечным ключом."));}
  else if(s.getValue(FUELED)&&!s.getValue(RUNNING)&&held.is(ModItems.WRENCH.get())){l.setBlock(p,s.setValue(RUNNING,true),3);l.players().forEach(x->x.sendSystemMessage(Component.literal("§aГенератор запущен! Ищите выход.")));}
  else player.sendSystemMessage(Component.literal(s.getValue(RUNNING)?"§aГенератор уже работает.":s.getValue(FUELED)?"§eНужен гаечный ключ.":"§7Сначала нужна канистра топлива."));
  return InteractionResult.CONSUME;
 }
}