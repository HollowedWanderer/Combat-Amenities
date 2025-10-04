package net.hollowed.combatamenities.util.entities;

import net.minecraft.util.StringIdentifiable;

public enum ExtraSlots implements StringIdentifiable {
    BACKSLOT(Type.HUMANOID_ARMOR, 0, 0, "backslot"),
    BELTSLOT(Type.HUMANOID_ARMOR, 1, 1, "beltslot");

    public static final StringIdentifiable.EnumCodec<ExtraSlots> CODEC = StringIdentifiable.createCodec(ExtraSlots::values);
    private final Type type;
    private final int entityId;
    private final String name;

    @SuppressWarnings("unused")
    ExtraSlots(final Type type, final int entityId, final int maxCount, final int index, final String name) {
        this.type = type;
        this.entityId = entityId;
        this.name = name;
    }

    ExtraSlots(final Type type, final int entityId, final int index, final String name) {
        this(type, entityId, 0, index, name);
    }

    public Type getType() {
        return this.type;
    }

    public int getOffsetEntitySlotId(int offset) {
        return offset + this.entityId;
    }

    public String asString() {
        return this.name;
    }

    public enum Type {
        HUMANOID_ARMOR;
        Type() {}
    }
}
