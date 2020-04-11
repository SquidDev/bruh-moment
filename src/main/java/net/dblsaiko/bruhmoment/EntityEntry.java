package net.dblsaiko.bruhmoment;

import java.util.Objects;

public class EntityEntry {

    public final IdentifierFilter entityId;
    public final IdentifierFilter itemId;

    public EntityEntry(IdentifierFilter entityId, IdentifierFilter itemId) {
        this.entityId = entityId;
        this.itemId = itemId;
    }

    public static EntityEntry fromArgs(String[] args) {
        if (args.length < 1) return null;
        IdentifierFilter entityId = IdentifierFilter.from(args[0]);
        IdentifierFilter itemId = IdentifierFilter.any();
        if (args.length > 1) {
            itemId = IdentifierFilter.from(args[1]);
        }
        return new EntityEntry(entityId, itemId);
    }

    public String[] toArgs() {
        return new String[]{entityId.toString(), itemId.asString()};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityEntry that = (EntityEntry) o;
        return Objects.equals(entityId, that.entityId) &&
            Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityId, itemId);
    }

    @Override
    public String toString() {
        return String.format("EntityEntry { entityId: %s, itemId: %s }", entityId, itemId);
    }

}
