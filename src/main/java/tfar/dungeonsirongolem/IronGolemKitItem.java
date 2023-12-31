package tfar.dungeonsirongolem;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import tfar.dungeonsirongolem.entity.DungeonsIronGolemEntity;

import java.util.*;

public class IronGolemKitItem extends Item {
    public IronGolemKitItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        BlockHitResult blockhitresult = getPlayerPOVHitResult(pLevel, pPlayer, ClipContext.Fluid.SOURCE_ONLY);
        if (blockhitresult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(itemstack);
        } else if (!(pLevel instanceof ServerLevel)) {
            return InteractionResultHolder.success(itemstack);
        } else {
            return InteractionResultHolder.pass(itemstack);
        }
    }
    public InteractionResult useOn(UseOnContext pContext) {
        Level level = pContext.getLevel();
        if (!(level instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        } else {
            ItemStack itemstack = pContext.getItemInHand();


            Player player = pContext.getPlayer();

            if (player == null) {
                return InteractionResult.SUCCESS;
            }

            UUID playerGolemUUID = uuidBasedOnPlayer(player);

            //there is a golem bound to this player
            ///pContext.getPlayer().sendSystemMessage(Component.translatable("This golem is already in use"));

            if (dead_golem_ids.containsKey(playerGolemUUID)) {
                pContext.getPlayer().sendSystemMessage(Component.translatable("This golem is on death cooldown"));
                return InteractionResult.SUCCESS;
            }

            Entity entity = ((ServerLevel) level).getEntity(playerGolemUUID);

            //the golem is loaded, teleport to player
            if (entity instanceof DungeonsIronGolemEntity) {
                BlockPos blockpos = pContext.getClickedPos();
                Direction direction = pContext.getClickedFace();
                BlockState blockstate = level.getBlockState(blockpos);

                BlockPos blockpos1;
                if (blockstate.getCollisionShape(level, blockpos).isEmpty()) {
                    blockpos1 = blockpos;
                } else {
                    blockpos1 = blockpos.relative(direction);
                }
                entity.setPos(blockpos1.getX(), blockpos1.getY() + 1, blockpos1.getZ());
                return InteractionResult.SUCCESS;
            } else {
                //the golem is not loaded
                BlockPos blockpos = pContext.getClickedPos();
                Direction direction = pContext.getClickedFace();
                BlockState blockstate = level.getBlockState(blockpos);

                BlockPos blockpos1;
                if (blockstate.getCollisionShape(level, blockpos).isEmpty()) {
                    blockpos1 = blockpos;
                } else {
                    blockpos1 = blockpos.relative(direction);
                }

                DungeonsIronGolemEntity spawn = DungeonsIronGolem.Entities.DUNGEONS_IRON_GOLEM_ENTITY.create(level);
                if (spawn != null) {
                    spawn.setPos(blockpos1.getX(),blockpos1.getY(),blockpos1.getZ());
                    spawn.setUUID(playerGolemUUID);
             //       System.out.println(spawn.getUUID());
                    level.addFreshEntity(spawn);
                    level.gameEvent(pContext.getPlayer(), GameEvent.ENTITY_PLACE, blockpos);
                    itemstack.getOrCreateTag().putUUID(GOLEM_ID, spawn.getUUID());
                    spawn.setOwnerUUID(pContext.getPlayer().getUUID());
                   // DungeonsIronGolem.ironGolemSavedData.addGolem(spawn.getUUID());
                    ((ServerPlayer) pContext.getPlayer()).connection.send(new ClientboundEntityEventPacket(spawn, (byte) 60));
                    spawn.level().playSound(null, spawn.blockPosition(), SoundEvents.IRON_GOLEM_REPAIR, SoundSource.NEUTRAL, 1, 1);
                }
            }
        }
        return InteractionResult.CONSUME;
    }

    public static UUID uuidBasedOnPlayer(Player player) {
        UUID uuid = player.getUUID();
        Random random = new Random();
        random.setSeed(uuid.getLeastSignificantBits() + DungeonsIronGolem.MODID.hashCode());
        long upper = random.nextLong();
        long lower = random.nextLong();
        return new UUID(upper,lower);
    }

    public static final int COOLDOWN = 1800;
    public static final String GOLEM_ID = "golem_id";
    private static final Map<UUID,Integer> dead_golem_ids = new HashMap<>();

    public static void addDeathCooldown(UUID uuid) {
        dead_golem_ids.put(uuid,COOLDOWN);
    }

    public static void tickDeathCooldowns(MinecraftServer server) {
        for (Iterator<Map.Entry<UUID, Integer>> iterator = dead_golem_ids.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<UUID, Integer> entry = iterator.next();
            UUID key = entry.getKey();
            Integer value = entry.getValue();
            dead_golem_ids.put(key, value - 1);
            if (value < 0) {
                iterator.remove();
            }
        }
    }
}
