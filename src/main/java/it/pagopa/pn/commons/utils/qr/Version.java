package it.pagopa.pn.commons.utils.qr;

import org.jetbrains.annotations.NotNull;

public record Version(int major, int minor, int patch) implements Comparable<Version> {

    @Override
    public int compareTo(@NotNull Version other) {
        int result = Integer.compare(this.major, other.major);
        if (result != 0) return result;

        result = Integer.compare(this.minor, other.minor);
        if (result != 0) return result;

        return Integer.compare(this.patch, other.patch);
    }

    @Override
    public @NotNull String toString() {
        return major + "." + minor + "." + patch;
    }
}
