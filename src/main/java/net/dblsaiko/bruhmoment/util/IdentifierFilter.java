package net.dblsaiko.bruhmoment.util;

import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import java.util.Objects;

public interface IdentifierFilter {
    static IdentifierFilter any() {
        return Impl.Any.INSTANCE;
    }

    static IdentifierFilter including(Identifier id) {
        return new Impl.Including(id);
    }

    static IdentifierFilter excluding(Identifier id) {
        return new Impl.Excluding(id);
    }

    static IdentifierFilter from(String string) {
        if ("-".equals(string)) {
            return any();
        } else if (string.startsWith("!")) {
            return excluding(Impl.tryParseId(string.substring(1)));
        } else {
            return including(Impl.tryParseId(string));
        }
    }

    boolean matches(Identifier id);

    String asString();

    class Impl {
        private Impl() {
        }

        private static Identifier tryParseId(String s ) {
            try {
                return new Identifier(s);
            } catch (InvalidIdentifierException e) {
                return new Identifier("", "");
            }
        }

        private static class Any implements IdentifierFilter {
            public static final Any INSTANCE = new Any();

            private Any() {
            }

            @Override
            public boolean matches(Identifier id) {
                return true;
            }

            @Override
            public String asString() {
                return "-";
            }
        }

        private static class Including implements IdentifierFilter {
            private final Identifier id;

            public Including(Identifier id) {
                this.id = id;
            }

            @Override
            public boolean matches(Identifier id) {
                return this.id.equals(id);
            }

            @Override
            public String asString() {
                return this.id.toString();
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || this.getClass() != o.getClass()) return false;
                Including including = (Including) o;
                return Objects.equals(this.id, including.id);
            }

            @Override
            public int hashCode() {
                return Objects.hash(this.id);
            }

            @Override
            public String toString() {
                return String.format("IdentifierFilter::Including { id: %s }", this.id);
            }
        }

        private static class Excluding implements IdentifierFilter {
            private final Identifier id;

            public Excluding(Identifier id) {
                this.id = id;
            }

            @Override
            public boolean matches(Identifier id) {
                return !this.id.equals(id);
            }

            @Override
            public String asString() {
                return String.format("!%s", this.id);
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || this.getClass() != o.getClass()) return false;
                Excluding excluding = (Excluding) o;
                return Objects.equals(this.id, excluding.id);
            }

            @Override
            public int hashCode() {
                return Objects.hash(this.id);
            }

            @Override
            public String toString() {
                return String.format("IdentifierFilter::Excluding { id: %s }", this.id);
            }
        }
    }
}
