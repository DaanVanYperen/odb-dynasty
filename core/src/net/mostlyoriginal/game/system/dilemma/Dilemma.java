package net.mostlyoriginal.game.system.dilemma;

/**
 * Created by Daan on 10-9-2014.
 */
public class Dilemma {

    public String id;
    public String[] text;
    public Choice[] choices;
    public String[] groups;

	public Dilemma() {
	}

	public static class Choice {
        public String[] label;
        public String[] success;
        public String[] failure;

		public Choice() {
		}

        /** Chance of success. If no failure set, always success! */
        public int risk = 20;
    }

    public static enum DilemmaGroup {
        SCRIPTED,
        POSITIVE,
        NEGATIVE
    }
}
