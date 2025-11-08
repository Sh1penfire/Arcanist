package arcanist.content;

import necesse.gfx.gameTexture.GameTexture;

public class ModTextures {

    public static GameTexture sawTooth;

    public static void load() {

        sawTooth = GameTexture.fromFile("projectiles/acn_saw_tooth");
    }
}
