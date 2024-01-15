package it.angrybear.serializers;

/**
 * An implementation of {@link CharCodeSerializer} that uses <i>§</i> as charCode.
 */
public class SectionSignSerializer extends CharCodeSerializer {

    /**
     * Instantiates a new Section sign serializer.
     */
    public SectionSignSerializer() {
        super("§");
    }
}
