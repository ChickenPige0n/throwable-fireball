package net.fabricmc.example.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.FireChargeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FireChargeItem.class)
public class FireChargeItemMixin extends Item {


    public FireChargeItemMixin(Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        ItemStack itemStack = user.getStackInHand(hand);

        Random random = world.getRandom();
        world.playSound(null, BlockPos.ofFloored(user.getPos()), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);

        if (!world.isClient) {
            double yaw = Math.toRadians(user.getYaw());
            double pitch = Math.toRadians(user.getPitch());

            double x = -Math.sin(yaw) * Math.cos(pitch);
            double y = -Math.sin(pitch);
            double z = Math.cos(yaw) * Math.cos(pitch);

            SmallFireballEntity smallFireballEntity = new SmallFireballEntity(world, user, x, y, z);
            smallFireballEntity.setPosition(smallFireballEntity.getX() + x, user.getBodyY(0.5) + 0.5 + y, smallFireballEntity.getZ() + z);
            world.spawnEntity(smallFireballEntity);
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }

        return TypedActionResult.success(itemStack, world.isClient());
    }
}
