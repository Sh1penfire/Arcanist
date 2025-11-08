package arcanist.content;

import necesse.engine.sound.gameSound.GameSound;

public class ModSounds {
    public static GameSound sawCharge;

    public static void load(){

        sawCharge = GameSound.fromFile("saw_charge");
    }
}
