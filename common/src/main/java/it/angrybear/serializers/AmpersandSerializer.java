package it.angrybear.serializers;

/**
 * An implementation of {@link CharCodeSerializer} that uses <i>&</i> as charCode.
 */
public class AmpersandSerializer extends CharCodeSerializer {

    /**
     * Instantiates a new Ampersand serializer.
     */
    public AmpersandSerializer() {
        super("&");
    }
}
