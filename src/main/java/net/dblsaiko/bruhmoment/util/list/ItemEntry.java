package net.dblsaiko.bruhmoment.util.list;

import java.util.Objects;

import net.dblsaiko.bruhmoment.util.IdentifierFilter;

public class ItemEntry {

    public final IdentifierFilter itemId;

    public ItemEntry(IdentifierFilter entityId) {
        this.itemId = entityId;
    }

    public static ItemEntry fromArgs(String[] args) {
        if (args.length < 1) return null;
        IdentifierFilter entityId = IdentifierFilter.from(args[0]);
        return new ItemEntry(entityId);
    }

    public String[] toArgs() {
        return new String[]{itemId.asString()};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemEntry that = (ItemEntry) o;
        return Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId);
    }

    @Override
    public String toString() {
        return String.format("ItemEntry { itemId: %s }", itemId);
    }

}
