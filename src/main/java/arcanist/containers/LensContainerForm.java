package arcanist.containers;

import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.containerSlot.FormContainerMaterialSlot;
import necesse.gfx.forms.components.lists.FormContainerCraftingList;
import necesse.gfx.forms.presets.containerComponent.ContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.UpgradeStationContainerForm;

public class LensContainerForm extends ContainerForm<LensContainer> {
    public LensContainerForm(Client client, LensContainer container) {
        super(client, 600, 200, container);
        FormFlow flow = new FormFlow(10);

        this.addComponent(flow.nextY(new FormContainerMaterialSlot(client, container, container.GENERATOR_SLOT, this.getWidth() / 2 - 20, 10), 10));
        this.addComponent(flow.nextY(new FormContainerMaterialSlot(client, container, container.AMP_SLOT, this.getWidth() / 2 - 20, 10), 10));
    }
}
