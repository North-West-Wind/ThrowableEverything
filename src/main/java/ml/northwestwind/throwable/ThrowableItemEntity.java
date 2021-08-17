package ml.northwestwind.throwable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class ThrowableItemEntity extends ThrowableItemProjectile {
    private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.defineId(ThrowableItemEntity.class, EntityDataSerializers.ITEM_STACK);
    private int age;
    private final float bobOffs;

    public ThrowableItemEntity(EntityType<ThrowableItemEntity> type, Level world) {
        super(type, world);
        this.bobOffs = this.random.nextFloat() * (float)Math.PI * 2.0F;
    }

    public ThrowableItemEntity(LivingEntity entity, Level world, ItemStack stack) {
        this(ThrowableEverything.RegistryEvents.EntityTypes.THROWABLE, world);
        setOwner(entity);
        setItem(stack);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(DATA_ITEM, ItemStack.EMPTY);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> parameter) {
        super.onSyncedDataUpdated(parameter);
        if (DATA_ITEM.equals(parameter)) this.getItem().setEntityRepresentation(this);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level.isClientSide) {
            this.level.broadcastEntityEvent(this, (byte)3);
            this.remove(RemovalReason.DISCARDED);
            ItemEntity itemEntity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), getItem());
            this.level.addFreshEntity(itemEntity);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        double dmg = 1;
        dmg += getItem().getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE).stream().mapToDouble(AttributeModifier::getAmount).sum();
        dmg *= getItem().getCount();
        dmg /= getItem().getMaxStackSize() / 4f;
        entity.hurt(DamageSource.thrown(this, this.getOwner()), (float) dmg);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
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
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.put("Item", getItem().save(new CompoundTag()));
        nbt.putShort("Age", (short)this.age);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        setItem(ItemStack.of(nbt.getCompound("Item")));
        this.age = nbt.getShort("Age");
    }

    @Override
    public void tick() {
        super.tick();
        if (this.age != -32768) {
            ++this.age;
        } else this.discard();
    }

    public float getSpin(float p_32009_) {
        return (this.age + p_32009_) / 20.0F + this.bobOffs;
    }
}
