package net.hollowed.combatamenities.util;

import com.mojang.serialization.Codec;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public class EntityEquipment {
    public static final Codec<EntityEquipment> CODEC;
    private final EnumMap<ExtraSlots, ItemStack> map;

    private EntityEquipment(EnumMap<ExtraSlots, ItemStack> map) {
        this.map = map;
    }

    public EntityEquipment() {
        this(new EnumMap<>(ExtraSlots.class));
    }

    public ItemStack put(ExtraSlots slot, ItemStack stack) {
        stack.getItem().postProcessComponents(stack);
        return Objects.requireNonNullElse(this.map.put(slot, stack), ItemStack.EMPTY);
    }

    public ItemStack get(ExtraSlots slot) {
        return this.map.getOrDefault(slot, ItemStack.EMPTY);
    }

    public boolean isEmpty() {
        Iterator<ItemStack> var1 = this.map.values().iterator();

        ItemStack itemStack;
        do {
            if (!var1.hasNext()) {
                return true;
            }

            itemStack = var1.next();
        } while(itemStack.isEmpty());

        return false;
    }

    public void copyFrom(EntityEquipment equipment) {
        this.map.clear();
        this.map.putAll(equipment.map);
    }

    public void dropAll(LivingEntity entity) {

        for (ItemStack itemStack : this.map.values()) {
            entity.dropItem(itemStack, true, false);
        }

        this.clear();
    }

    public void clear() {
        this.map.replaceAll((slot, stack) -> ItemStack.EMPTY);
    }

    static {
        CODEC = Codec.unboundedMap(ExtraSlots.CODEC, ItemStack.CODEC).xmap((map) -> {
            EnumMap<ExtraSlots, ItemStack> enumMap = new EnumMap<>(ExtraSlots.class);
            enumMap.putAll(map);
            return new EntityEquipment(enumMap);
        }, (equipment) -> {
            Map<ExtraSlots, ItemStack> map = new EnumMap<>(equipment.map);
            map.values().removeIf(ItemStack::isEmpty);
            return map;
        });
    }
}
