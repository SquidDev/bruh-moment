package net.dblsaiko.bruhmoment;

import java.util.Objects;

public class BlockEntry {

    public final IdentifierFilter blockId;
    public final IdentifierFilter itemId;

    public BlockEntry(IdentifierFilter blockId, IdentifierFilter itemId) {
        this.blockId = blockId;
        this.itemId = itemId;
    }

    public static BlockEntry fromArgs(String[] args) {
        if (args.length < 1) return null;
        IdentifierFilter blockId = IdentifierFilter.from(args[0]);
        IdentifierFilter itemId = IdentifierFilter.any();
        if (args.length > 1) {
            itemId = IdentifierFilter.from(args[1]);
        }
        return new BlockEntry(blockId, itemId);
    }

    public String[] toArgs() {
        return new String[]{blockId.toString(), itemId.asString()};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockEntry that = (BlockEntry) o;
        return Objects.equals(blockId, that.blockId) &&
            Objects.equals(itemId, that.itemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockId, itemId);
    }

    @Override
    public String toString() {
        return String.format("BlockEntry { blockId: %s, itemId: %s }", blockId, itemId);
    }

}
