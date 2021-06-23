package ml.northwestwind.throwable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
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
            RenderingRegistry.registerEntityRenderingHandler(EntityTypes.THROWABLE, manager -> new ThrowableRenderer(manager, Minecraft.getInstance().getItemRenderer()));
        }

        public static class EntityTypes {
            public static final EntityType<ThrowableItemEntity> THROWABLE = (EntityType<ThrowableItemEntity>) EntityType.Builder.<ThrowableItemEntity>of(ThrowableItemEntity::new, EntityClassification.MISC).sized(0.25f, 0.25f).build("throwable").setRegistryName(MOD_ID, "throwable");
        }
    }

    @Mod.EventBusSubscriber
    public static class OtherEvents {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void itemToss(final ItemTossEvent event) {
            event.setCanceled(true);
            PlayerEntity player = event.getPlayer();
            ThrowableItemEntity entity = new ThrowableItemEntity(player, player.level, event.getEntityItem().getItem());
            Vector3d pos = event.getEntity().position().add(player.getLookAngle());
            entity.setPos(pos.x, pos.y, pos.z);
            entity.shootFromRotation(player, player.xRot, player.yRot, 0, 1.5f, 1);
            player.level.addFreshEntity(entity);
        }
    }
}
