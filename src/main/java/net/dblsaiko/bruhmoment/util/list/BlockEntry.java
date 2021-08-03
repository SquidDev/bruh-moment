package net.dblsaiko.bruhmoment.util.list;

import net.dblsaiko.bruhmoment.util.IdentifierFilter;

public record BlockEntry(IdentifierFilter blockId, IdentifierFilter itemId) {
    public static BlockEntry fromArgs(String[] args) {
        if (args.length < 1) {
            return null;
        }

        IdentifierFilter blockId = IdentifierFilter.from(args[0]);
        IdentifierFilter itemId = IdentifierFilter.any();

        if (args.length > 1) {
            itemId = IdentifierFilter.from(args[1]);
        }

        return new BlockEntry(blockId, itemId);
    }

    public String[] toArgs() {
        return new String[] { this.blockId.asString(), this.itemId.asString() };
    }

    @Override
    public String toString() {
        return String.format("BlockEntry { blockId: %s, itemId: %s }", this.blockId, this.itemId);
    }
}
