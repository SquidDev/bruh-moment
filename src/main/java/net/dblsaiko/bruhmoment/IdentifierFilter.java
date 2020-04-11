package net.dblsaiko.bruhmoment;

import net.minecraft.util.Identifier;

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
        if ("-".equals(string)) return any();
        else if (string.startsWith("!")) return excluding(new Identifier(string.substring(1)));
        else return including(new Identifier(string));
    }

    boolean matches(Identifier id);

    String asString();

    class Impl {

        private Impl() {
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
                return id.toString();
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Including including = (Including) o;
                return Objects.equals(id, including.id);
            }

            @Override
            public int hashCode() {
                return Objects.hash(id);
            }

            @Override
            public String toString() {
                return String.format("IdentifierFilter::Including { id: %s }", id);
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
                return String.format("!%s", id);
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Excluding excluding = (Excluding) o;
                return Objects.equals(id, excluding.id);
            }

            @Override
            public int hashCode() {
                return Objects.hash(id);
            }

            @Override
            public String toString() {
                return String.format("IdentifierFilter::Excluding { id: %s }", id);
            }

        }

    }


}
