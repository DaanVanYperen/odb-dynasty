package net.mostlyoriginal.game.system.dilemma;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.GroupManager;
import com.artemis.systems.EntityProcessingSystem;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Json;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.util.GdxUtil;
import net.mostlyoriginal.api.utils.EntityUtil;
import net.mostlyoriginal.game.GdxArtemisGame;
import net.mostlyoriginal.game.component.dilemma.DilemmaChoice;
import net.mostlyoriginal.game.component.ui.*;
import net.mostlyoriginal.game.system.resource.StockpileSystem;

import java.util.List;

/**
 * Responsible for serving and processing dilemmas.
 *
 * @author Daan van Yperen
 */
public class DilemmaSystem extends EntityProcessingSystem {

    private DilemmaLibrary dilemmaLibrary;

    public static final String DILEMMA_GROUP = "dilemma";
    public static final int ROW_HEIGHT = 9;

    public static final Color COLOR_DILEMMA = Color.valueOf("6AD7ED");
    public static final String COLOR_RAW_BRIGHT = "E7E045";
    public static final String COLOR_RAW_DIMMED = "FDF1AA";
    private boolean dilemmaActive;

    private GroupManager groupManager;
    private StockpileSystem stockpileSystem;
    private M<Tint> mColor;
    private M<Pos> mPos;

    public DilemmaSystem() {
        super(Aspect.all(Pos.class, DilemmaChoice.class));
    }

    public Entity createLabel(int x, int y, Color color, String text) {
        Entity entity = new EntityBuilder(world)
                .with(Pos.class, Renderable.class, Tint.class)
                .with(new Label(text)).group(DILEMMA_GROUP).build();
        mPos.get(entity).xy.set(x,y);
        mColor.get(entity).set(color);
        return entity;
    }

    private Entity createOption(int x, int y, String text, ButtonListener listener) {
        //createLabel(x, y, COLOR_DILEMMA, text);
        Entity entity = new EntityBuilder(world)
                .with(Pos.class, Renderable.class, Tint.class).with(
                new Bounds(0, -8, text.length() * 8, 0),
                new Clickable(),
                new Button(COLOR_RAW_DIMMED, COLOR_RAW_BRIGHT, "FFFFFF", listener),
                new Label(text)
        )
                .group(DILEMMA_GROUP).build();
        mPos.get(entity).xy.set(x,y);
        return entity;
    }

    public boolean isDilemmaActive() {
        return dilemmaActive;
    }

    @Override
    protected void initialize() {
        super.initialize();
        loadDilemmas();
    }

    private void loadDilemmas() {
        final Json json = new Json();
        dilemmaLibrary = json.fromJson(DilemmaLibrary.class, Gdx.files.internal("dilemma.json"));
        dilemmaLibrary.assignToGroups();
    }

    private void startDilemma(String dilemmaId) {
        Dilemma dilemma = dilemmaLibrary.getById(dilemmaId);
        if (dilemma == null) {
            throw new RuntimeException("Missing dilemma logic for " + dilemmaId);
        }
        startDilemma(dilemma);
    }

    private Dilemma startDilemma(Dilemma dilemma) {
        if (!dilemmaActive) {
            int row = Math.max(4, dilemma.choices.length + dilemma.text.length);

            dilemmaActive = true;
            for (String text : dilemma.text) {
                createLabel(50, 50 + ROW_HEIGHT * row, COLOR_DILEMMA, text);
                row--;
            }

            for (Dilemma.Choice choice : dilemma.choices) {

                // random chance of succes, if no failure options defined, always failure.
                final String[] choices = (choice.failure == null) || (MathUtils.random(0, 100) < 100 - choice.risk) ? choice.success : choice.failure;

                createOption(50, 50 + ROW_HEIGHT * row, "[" + choice.label[MathUtils.random(0, choice.label.length - 1)] + "]", new DilemmaListener(choices));
                row--;
            }
        }

        return dilemma;
    }

    /**
     * Remove active dilemma from screen.
     */
    private void stopDilemma() {
        EntityUtil.safeDeleteAll(groupManager.getEntities(DILEMMA_GROUP));
        dilemmaActive = false;
    }


    @Override
    protected void process(Entity e) {
    }

    /**
     * Spawn a dilemma, with a bias towards positive dilemmas.
     */
    public void randomDilemma() {
        randomDilemma(60);
    }

    private void randomDilemma(int positiveChance) {
        if (MathUtils.random(0, 99) < positiveChance) {
            startRandomDilemmaFromGroup("positive");
        } else {
            startRandomDilemmaFromGroup("negative");
        }
    }

    private void startRandomDilemmaFromGroup(String group) {
        List<Dilemma> dilemmas = dilemmaLibrary.getGroup(group);

        Dilemma dilemma = null;
        while (dilemma == null) {
            dilemma = dilemmas.get(MathUtils.random(0, dilemmas.size() - 1));

            if (dilemma != null) {
                dilemma = startDilemma(dilemma);
                // if startdilemma fails, it returns NULL and we will search again.
            }
        }
    }

    /**
     * Just closes dilemma, no action
     */
    private class CloseDilemmaListener extends ButtonListener {
        @Override
        public void run() {
            stopDilemma();
        }
    }

    public final String[] DEFAULT_ACTION = new String[]{"CLOSE"};

    private class DilemmaListener extends ButtonListener {

        private String[] actions;

        public DilemmaListener(String[] actions) {
            super();
            this.actions = (actions == null || actions.length == 0) ? DEFAULT_ACTION : actions;
        }

        @Override
        public void run() {
            super.run();

            // run all success.
            for (String action : actions) {
                triggerAction(action);
            }
        }
    }

    /**
     * Trigger hardcodede action indicated by string. If not exists, assume we are starting a dilemma.
     */
    private void triggerAction(String action) {
        stopDilemma();
        switch (action) {
            case "CLOSE":
                break;
            case "RESTART":
                restartGame();
                break;
            default:
                startDilemma(action);
                break;
        }
    }

    private static void restartGame() {
        GdxArtemisGame.getInstance().restart();
    }
}
