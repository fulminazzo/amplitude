package it.fulminazzo.amplitude.serializer;

/**
 * An implementation of {@link CharCodeSerializer} that uses <i>&amp;</i> as charCode.
 */
public final class AmpersandSerializer extends CharCodeSerializer {

    /**
     * Instantiates a new Ampersand serializer.
     */
    public AmpersandSerializer() {
        super("&");
    }

}
