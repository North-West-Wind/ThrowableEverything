package ml.northwestwind.throwable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class ThrowableItemEntity extends ProjectileItemEntity {
    private static final DataParameter<ItemStack> DATA_ITEM = EntityDataManager.defineId(ThrowableItemEntity.class, DataSerializers.ITEM_STACK);

    public ThrowableItemEntity(EntityType<ThrowableItemEntity> type, World world) {
        super(type, world);
    }

    public ThrowableItemEntity(LivingEntity entity, World world, ItemStack stack) {
        super(ThrowableEverything.RegistryEvents.EntityTypes.THROWABLE, entity, world);
        setItem(stack);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_ITEM, ItemStack.EMPTY);
    }

    @Override
    public void onSyncedDataUpdated(DataParameter<?> parameter) {
        super.onSyncedDataUpdated(parameter);
        if (DATA_ITEM.equals(parameter)) this.getItem().setEntityRepresentation(this);
    }

    @Override
    protected void onHit(RayTraceResult result) {
        super.onHit(result);
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte)3);
            this.remove();
            ItemEntity itemEntity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), getItem());
            this.level.addFreshEntity(itemEntity);
        }
    }

    @Override
    protected void onHitEntity(EntityRayTraceResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        double dmg = 1;
        if (getItem().getItem() instanceof ToolItem) dmg += ((ToolItem) getItem().getItem()).getAttackDamage();
        dmg += getItem().getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_DAMAGE).stream().mapToDouble(AttributeModifier::getAmount).sum();
        dmg *= getItem().getCount();
        dmg /= getItem().getMaxStackSize() / 4f;
        entity.hurt(DamageSource.thrown(this, this.getOwner()), (float) dmg);
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected Item getDefaultItem() {
        return getItem().getItem();
    }

    @Override
    public ItemStack getItem() {
        return this.getEntityData().get(DATA_ITEM);
    }

    @Override
    public void setItem(ItemStack stack) {
        super.setItem(stack);
        this.getEntityData().set(DATA_ITEM, stack);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.put("Item", getItem().save(new CompoundNBT()));
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT nbt) {
        super.readAdditionalSaveData(nbt);
        setItem(ItemStack.of(nbt.getCompound("Item")));
    }
}
