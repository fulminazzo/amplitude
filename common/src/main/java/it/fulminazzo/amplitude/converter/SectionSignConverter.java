package it.fulminazzo.amplitude.converter;

/**
 * An implementation of {@link CharCodeConverter} that uses <i>§</i> as charCode.
 */
public final class SectionSignConverter extends CharCodeConverter {

    /**
     * Instantiates a new Section sign serializer.
     */
    public SectionSignConverter() {
        super("§");
    }

}
