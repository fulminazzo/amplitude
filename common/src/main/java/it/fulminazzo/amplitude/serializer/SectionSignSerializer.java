package it.fulminazzo.amplitude.serializer;

/**
 * An implementation of {@link CharCodeSerializer} that uses <i>§</i> as charCode.
 */
public final class SectionSignSerializer extends CharCodeSerializer {

    /**
     * Instantiates a new Section sign serializer.
     */
    public SectionSignSerializer() {
        super("§");
    }

}
