package arcanist.containers;

import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.containerSlot.FormContainerEnchantSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerMaterialSlot;
import necesse.gfx.forms.components.containerSlot.FormContainerTrashSlot;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;

public class LensContainerForm extends ContainerForm<LensContainer> {
    public LensContainerForm(Client client, LensContainer container) {
        super(client, 600, 200, container);
        FormFlow flow = new FormFlow(10);

        this.addComponent(flow.nextY(new FormContainerMaterialSlot(client, container, container.LENS_SLOT, this.getWidth() / 2 - 20, 10), 10));
        this.addComponent(flow.nextY(new FormContainerMaterialSlot(client, container, container.GENERATOR_SLOT, this.getWidth() / 2 - 20, 10), 10));

    }
}
