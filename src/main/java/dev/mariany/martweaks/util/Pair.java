package dev.mariany.martweaks.util;

import java.util.Objects;

public class Pair<A, B> {
    private A left;
    private B right;

    public Pair(A left, B right) {
        this.left = left;
        this.right = right;
    }

    public static <C, D> Pair<C, D> of(C left, D right) {
        return new Pair<>(left, right);
    }

    public A getLeft() {
        return this.left;
    }

    public B getRight() {
        return this.right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(left, pair.left) && Objects.equals(right, pair.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}
