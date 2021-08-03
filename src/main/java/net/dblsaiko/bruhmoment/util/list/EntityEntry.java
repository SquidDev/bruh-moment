package net.dblsaiko.bruhmoment.util.list;

import net.dblsaiko.bruhmoment.util.IdentifierFilter;

public record EntityEntry(IdentifierFilter entityId, IdentifierFilter itemId) {
    public static EntityEntry fromArgs(String[] args) {
        if (args.length < 1) {
            return null;
        }

        IdentifierFilter entityId = IdentifierFilter.from(args[0]);
        IdentifierFilter itemId = IdentifierFilter.any();

        if (args.length > 1) {
            itemId = IdentifierFilter.from(args[1]);
        }

        return new EntityEntry(entityId, itemId);
    }

    public String[] toArgs() {
        return new String[] { this.entityId.asString(), this.itemId.asString() };
    }

    @Override
    public String toString() {
        return String.format("EntityEntry { entityId: %s, itemId: %s }", this.entityId, this.itemId);
    }
}
