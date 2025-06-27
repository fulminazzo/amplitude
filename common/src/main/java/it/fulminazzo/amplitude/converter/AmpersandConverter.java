package it.fulminazzo.amplitude.converter;

/**
 * An implementation of {@link CharCodeConverter} that uses <i>&amp;</i> as charCode.
 */
public final class AmpersandConverter extends CharCodeConverter {

    /**
     * Instantiates a new Ampersand serializer.
     */
    public AmpersandConverter() {
        super("&");
    }

}
