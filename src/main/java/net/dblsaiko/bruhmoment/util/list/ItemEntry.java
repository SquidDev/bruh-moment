package net.dblsaiko.bruhmoment.util.list;

import net.dblsaiko.bruhmoment.util.IdentifierFilter;

public record ItemEntry(IdentifierFilter itemId) {
    public static ItemEntry fromArgs(String[] args) {
        if (args.length < 1) {
            return null;
        }

        IdentifierFilter entityId = IdentifierFilter.from(args[0]);
        return new ItemEntry(entityId);
    }

    public String[] toArgs() {
        return new String[] { this.itemId.asString() };
    }

    @Override
    public String toString() {
        return String.format("ItemEntry { itemId: %s }", this.itemId);
    }
}
