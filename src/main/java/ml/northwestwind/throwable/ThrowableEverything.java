package ml.northwestwind.throwable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(ThrowableEverything.MOD_ID)
public class ThrowableEverything
{
    public static final String MOD_ID = "throwable";

    public ThrowableEverything() {
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void registerEntityType(final RegistryEvent.Register<EntityType<?>> event) {
            event.getRegistry().registerAll(
                    EntityTypes.THROWABLE
            );
        }

        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent
        public static void clientSetup(final FMLClientSetupEvent event) {
            EntityRenderers.register(EntityTypes.THROWABLE, manager -> new ThrowableRenderer(manager, Minecraft.getInstance().getItemRenderer()));
        }

        public static class EntityTypes {
            public static final EntityType<ThrowableItemEntity> THROWABLE = (EntityType<ThrowableItemEntity>) EntityType.Builder.<ThrowableItemEntity>of(ThrowableItemEntity::new, MobCategory.MISC).sized(0.25f, 0.25f).build("throwable").setRegistryName(MOD_ID, "throwable");
        }
    }

    @Mod.EventBusSubscriber
    public static class OtherEvents {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void itemToss(final ItemTossEvent event) {
            event.setCanceled(true);
            Player player = event.getPlayer();
            ThrowableItemEntity entity = new ThrowableItemEntity(player, player.level, event.getEntityItem().getItem());
            Vec3 pos = event.getEntity().position().add(player.getLookAngle());
            entity.setPos(pos.x, pos.y, pos.z);
            entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0, 1.5f, 1);
            player.level.addFreshEntity(entity);
        }
    }
}
