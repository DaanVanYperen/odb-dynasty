package net.mostlyoriginal.game.system.dilemma;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.managers.GroupManager;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Json;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.basic.Scale;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.util.DynastyEntityBuilder;
import net.mostlyoriginal.api.utils.EntityUtil;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.GdxArtemisGame;
import net.mostlyoriginal.game.component.dilemma.DilemmaChoice;
import net.mostlyoriginal.game.component.ui.*;
import net.mostlyoriginal.game.manager.AssetSystem;
import net.mostlyoriginal.game.system.resource.StockpileSystem;

import java.util.List;

/**
 * Responsible for serving and processing dilemmas.
 *
 * @author Daan van Yperen
 */
public class DilemmaSystem extends EntityProcessingSystem {

    public static final int TEXT_ZOOM = G.ZOOM;
    private DilemmaLibrary dilemmaLibrary;

    public static final String DILEMMA_GROUP = "dilemma";
    public static final int ROW_HEIGHT = 16;

    public static final String COLOR_DILEMMA = "00000080";
    public static final String COLOR_RAW_BRIGHT = "86161f";
    public static final String COLOR_RAW_DIMMED = "ae121f";
    private boolean dilemmaActive;

    private GroupManager groupManager;
    private StockpileSystem stockpileSystem;
    private M<Tint> mColor;
    private M<Pos> mPos;
    private M<Scale> mScale;
    private M<Renderable> mRenderable;

    public DilemmaSystem() {
        super(Aspect.all(Pos.class, DilemmaChoice.class));
    }

    public Entity createLabel(int x, int y, String color, String text) {
        Entity e = new DynastyEntityBuilder(world)
                .with(new Label(text, TEXT_ZOOM))
                .group(DILEMMA_GROUP)
                .pos(x, y)
                .renderable(920)
                .scale(TEXT_ZOOM)
                .tint(color)
                .build();
        return e;
    }

    private Entity createOption(int x, int y, String text, ButtonListener listener) {
        //createLabel(x, y, COLOR_DILEMMA, text);
        Entity entity = new DynastyEntityBuilder(world)
                .with(Tint.class).with(
                        new Bounds(0, -8, text.length() * 8, 0),
                        new Clickable(),
                        new Button(COLOR_RAW_DIMMED, COLOR_RAW_BRIGHT, "FFFFFF", listener),
                        new Label(text, TEXT_ZOOM)
                )
                .group(DILEMMA_GROUP)
                .renderable(920)
                .pos(x, y)
                .scale(TEXT_ZOOM)
                .build();
        return entity;
    }

    public boolean isDilemmaActive() {
        return dilemmaActive;
    }

    @Override
    protected void initialize() {
        super.initialize();
        loadDilemmas();
        randomDilemma();
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
            int row = 0;

            int slabX = 7 * G.ZOOM;
            int slabY = 7 * G.ZOOM;

            DilemmaActor dilemmaActor = dilemmaLibrary.getActorById(dilemma.actor);
            createBackground(slabX, slabY,
                    dilemmaActor != null ? dilemmaActor.asset : null,
                    dilemmaActor != null ? dilemmaActor.name : null,
                    dilemmaActor != null ? dilemmaActor.role : null);

            int textMarginX = 15 * G.ZOOM;
            int textMarginY = 15 * G.ZOOM;

            int optionMarginY = 5 * G.ZOOM;

            dilemmaActive = true;
            for (String text : dilemma.text) {
                createLabel(slabX+ textMarginX, slabY - textMarginY + AssetSystem.SLAB_HEIGHT * G.ZOOM - ROW_HEIGHT * row, COLOR_DILEMMA, text);
                row++;
            }

            for (Dilemma.Choice choice : dilemma.choices) {

                // random chance of succes, if no failure options defined, always failure.
                final String[] choices = (choice.failure == null) || (MathUtils.random(0, 100) < 100 - choice.risk) ? choice.success : choice.failure;

                createOption(slabX+ textMarginX, slabY - textMarginY - optionMarginY + AssetSystem.SLAB_HEIGHT * G.ZOOM - ROW_HEIGHT * row, "] " + choice.label[MathUtils.random(0, choice.label.length - 1)], new DilemmaListener(choices));
                row++;
            }
        }

        return dilemma;
    }

    private void createBackground(int x, int y, String actorId, String actorName, String actorRole) {
        Entity slab =
                new DynastyEntityBuilder(world)
                        .pos(x, y)
                        .anim("SLAB")
                        .renderable(910)
                        .scale(G.ZOOM)
                        .group(DILEMMA_GROUP)
                        .build();

        int actorSlabOverlap = 9;
        int actorOffsetX = 14 * G.ZOOM;
        int actorOffsetY = (AssetSystem.SLAB_HEIGHT - actorSlabOverlap) * G.ZOOM;

        if ( actorId != null ) {
            Entity actor =
                    new DynastyEntityBuilder(world)
                            .pos(x + actorOffsetX, y + actorOffsetY)
                            .anim(actorId)
                            .renderable(908)
                            .scale(G.ZOOM)
                            .group(DILEMMA_GROUP)
                            .build();

            int actorVsScrollMargin = 2;
            int scrollSlabOverlap = 11;
            int scrollOffsetX = (AssetSystem.DEFAULT_ACTOR_WIDTH + actorVsScrollMargin) * G.ZOOM + actorOffsetX;
            int scrollOffsetY = (AssetSystem.SLAB_HEIGHT - scrollSlabOverlap) * G.ZOOM;

            Entity scroll =
                    new DynastyEntityBuilder(world)
                            .pos(x + scrollOffsetX, y + scrollOffsetY)
                            .anim("SCROLL")
                            .renderable(912)
                            .scale(G.ZOOM)
                            .group(DILEMMA_GROUP)
                            .build();

            int labelHeight = 8 * G.ZOOM;
            int labelMarginX = 12 * G.ZOOM;
            int labelMarginY = 8 * G.ZOOM;
            int labelOffsetY = scrollOffsetY + AssetSystem.SCROLL_HEIGHT * G.ZOOM;


            createLabel(x + scrollOffsetX + labelMarginX, y + labelOffsetY - labelMarginY, "3e2819", actorName);
            createLabel(x + scrollOffsetX + labelMarginX, y + labelOffsetY - labelHeight * 1 - labelMarginY, "333333", actorRole);
        }
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
     * pharao died
     */
    public void death() {
        stopDilemma();
        startDilemma("YOU_DEAD");
    }

    /**
     * best dynasty possible!
     */
    public void superDynasty() {
        stopDilemma();
        startDilemma("SUPER_DYNASTY");
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
            case "+AGE":
                stockpileSystem.alter(StockpileSystem.Resource.AGE, 1);
                break;
            case "+LIFESPAN":
                stockpileSystem.alter(StockpileSystem.Resource.LIFESPAN, 1);
                break;
            case "+WEALTH":
                stockpileSystem.alter(StockpileSystem.Resource.WEALTH, 1);
                break;
            case "+FOOD":
                stockpileSystem.alter(StockpileSystem.Resource.FOOD, 1);
                break;
            case "+WORKERS":
                stockpileSystem.alter(StockpileSystem.Resource.WORKERS, 1);
                break;
            case "+COMPLETION":
                stockpileSystem.alter(StockpileSystem.Resource.COMPLETION, 1);
                break;

            case "-LIFESPAN":
                stockpileSystem.alter(StockpileSystem.Resource.LIFESPAN, -1);
                break;
            case "-WEALTH":
                stockpileSystem.alter(StockpileSystem.Resource.WEALTH, -1);
                break;
            case "-FOOD":
                stockpileSystem.alter(StockpileSystem.Resource.FOOD, -1);
                break;
            case "-WORKERS":
                stockpileSystem.alter(StockpileSystem.Resource.WORKERS, -1);
                break;
            case "-COMPLETION":
                stockpileSystem.alter(StockpileSystem.Resource.COMPLETION, -1);
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
