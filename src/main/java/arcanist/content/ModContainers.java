package arcanist.content;

import arcanist.containers.LensContainer;
import arcanist.containers.LensContainerForm;
import necesse.engine.registries.ContainerRegistry;

public class ModContainers {
    public static int LENS_CONTAINER;
    public static void load(){
        LENS_CONTAINER = ContainerRegistry.registerContainer((client, uniqueSeed, packet) -> {
            return new LensContainerForm(client, new LensContainer(client.getClient(), uniqueSeed, packet));
        }, (client, uniqueSeed, packet, serverObject) -> {
            return new LensContainer(client, uniqueSeed, packet);
        });


        /*
        GLYPH_TRAP_CONTAINER = registerOEContainer((client, uniqueSeed, oe, content) -> {
            return new GlyphContainerForm(client, new GlyphTrapContainer(client.getClient(), uniqueSeed, (GlyphTrapObjectEntity)oe));
        }, (client, uniqueSeed, oe, content, serverObject) -> {
            return new GlyphTrapContainer(client, uniqueSeed, (GlyphTrapObjectEntity)oe);
        });
         */
    }
}
