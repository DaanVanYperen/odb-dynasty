package net.mostlyoriginal.game.system.dilemma;

import com.artemis.Aspect;
import com.artemis.Entity;
import com.artemis.managers.GroupManager;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Json;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.basic.Scale;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.operation.OperationFactory;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.util.DynastyEntityBuilder;
import net.mostlyoriginal.api.utils.EntityUtil;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.GdxArtemisGame;
import net.mostlyoriginal.game.component.dilemma.DilemmaChoice;
import net.mostlyoriginal.game.component.ui.*;
import net.mostlyoriginal.game.manager.AssetSystem;
import net.mostlyoriginal.game.manager.StructureSystem;
import net.mostlyoriginal.game.system.endgame.EndgameSystem;
import net.mostlyoriginal.game.system.logic.ProgressAlgorithmSystem;
import net.mostlyoriginal.game.system.render.LabelRenderSystem;
import net.mostlyoriginal.game.system.resource.FireballSystem;
import net.mostlyoriginal.game.system.resource.StockpileSystem;
import net.mostlyoriginal.game.system.ui.RiverDioramaSystem;

import java.util.List;

/**
 * Responsible for serving and processing dilemmas.
 *
 * @author Daan van Yperen
 */
public class DilemmaSystem extends EntityProcessingSystem {

    public static final int TEXT_ZOOM = G.ZOOM;
    public static final String DILEMMA_SHADOW_TEXT_COLOR = "9f9a9b";
    public static final String DILEMMA_SCROLL_SHADOW_COLOR = "f3b072";
    public static final int DISCIPLINE_FOLLOWUP_WAIT_TIME = 6;
    public static final int INITIAL_DISCIPLINE_WAIT_TIME = 6;
    private DilemmaLibrary dilemmaLibrary;

    public static final String DILEMMA_GROUP = "dilemma";
    public static final int ROW_HEIGHT = 16;

    public static final String COLOR_DILEMMA = "00000080";
    public static final String COLOR_RAW_BRIGHT = "ae121f";
    public static final String COLOR_RAW_DIMMED = "86161f";
    private boolean dilemmaActive;

    private GroupManager groupManager;
    private StockpileSystem stockpileSystem;
    private StructureSystem structureSystem;

    private M<Tint> mColor;
    private M<Pos> mPos;
    private M<Scale> mScale;
    private M<Renderable> mRenderable;
    private RiverDioramaSystem riverSystem;
    private ProgressAlgorithmSystem progressAlgorithmSystem;
    private LabelRenderSystem labelRenderSystem;
    private EndgameSystem endgameSystem;
    private FireballSystem fireballSystem;

    public DilemmaSystem() {
        super(Aspect.all(Pos.class, DilemmaChoice.class));
    }

    public float createLabel(int x, int y, String color, String text, String shadowTextColor, int maxWidth) {
        Label label = new Label(text, TEXT_ZOOM);
        label.shadowColor = new Tint(shadowTextColor);
        label.maxWidth = maxWidth;
        int insertDistanceY =AssetSystem.SLAB_HEIGHT*G.ZOOM;
        DynastyEntityBuilder builder = new DynastyEntityBuilder(world)
                .with(label)
                .group(DILEMMA_GROUP)
                .pos(x, y- insertDistanceY)
                .renderable(920)
                .scale(TEXT_ZOOM)
                .tint(color);

        builder.schedule(OperationFactory.tween(new Pos(x,y -insertDistanceY ),
                new Pos(x, y), 1f, Interpolation.pow4Out ));

        builder
                .build();
        return labelRenderSystem.estimateHeight(label);
    }

    private float createOption(int x, int y, String text, ButtonListener listener, int maxWidth) {
        //createLabel(x, y, COLOR_DILEMMA, text);
        Label label = new Label(text, TEXT_ZOOM);
        label.shadowColor = new Tint(DILEMMA_SHADOW_TEXT_COLOR);
        label.maxWidth = maxWidth;
        float height = labelRenderSystem.estimateHeight(label);
        int insertDistanceY =AssetSystem.SLAB_HEIGHT*G.ZOOM;
        DynastyEntityBuilder builder = new DynastyEntityBuilder(world)
                .with(Tint.class).with(
                        new Bounds(0, (int) -height, text.length() * 8, 0),
                        new Clickable(),
                        new Button(COLOR_RAW_DIMMED, COLOR_RAW_BRIGHT, COLOR_RAW_BRIGHT, listener),
                        label
                )
                .group(DILEMMA_GROUP)
                .renderable(920)
                .pos(x, y-insertDistanceY)
                .scale(TEXT_ZOOM);

        builder.schedule(OperationFactory.tween(new Pos(x,y-insertDistanceY),
                new Pos(x, y), 1f, Interpolation.pow4Out ));
        builder.build();
        return height;
    }

    public boolean isDilemmaActive() {
        return dilemmaActive;
    }

    @Override
    protected void initialize() {
        super.initialize();
        loadDilemmas();
        //startDebugDilemma();
    }

    public void startDebugDilemma() {
        startDilemmaConditionally("DEBUG");
    }

    private void startDilemmaConditionally(String dilemmaId) {
        Dilemma dilemma = dilemmaLibrary.getById(dilemmaId);
        if (dilemma != null && predicatesMet(dilemma)) {
            startDilemma(dilemma);
        }
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

            // run all immediate actions.
            if (dilemma.immediate != null) {
                for (String action : dilemma.immediate) {
                    triggerAction(action);
                }
            }

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
            int maxLabelWidth = AssetSystem.SLAB_WIDTH * G.ZOOM - 20 * G.ZOOM;

            for (String text : dilemma.text) {
                row+=2*G.ZOOM+createLabel(slabX + textMarginX, slabY - textMarginY + AssetSystem.SLAB_HEIGHT * G.ZOOM - row, COLOR_DILEMMA, replaceTokens(text), DILEMMA_SHADOW_TEXT_COLOR, maxLabelWidth);
            }

            for (Dilemma.Choice choice : dilemma.choices) {

                // random chance of succes, if no failure options defined, always failure.
                final String[] choices = (choice.failure == null) || (MathUtils.random(0, 100) < 100 - choice.risk) ? choice.success : choice.failure;

                String choiceText = "] " + choice.label[MathUtils.random(0, choice.label.length - 1)];
                row+= 3*G.ZOOM+createOption(slabX + textMarginX, slabY - textMarginY - optionMarginY + AssetSystem.SLAB_HEIGHT * G.ZOOM - row, choiceText, new DilemmaListener(choices), maxLabelWidth);
            }
        }

        return dilemma;
    }

    private String replaceTokens(String text) {
        return text.replace("{score}", ""+endgameSystem.getScore()).replace("{rank}", ""+endgameSystem.getSuccess().name());
    }

    private void createBackground(int x, int y, String actorId, String actorName, String actorRole) {

        int insertDistanceY =AssetSystem.SLAB_HEIGHT*G.ZOOM;

        Entity slab =
                new DynastyEntityBuilder(world)
                        .pos(x, y- insertDistanceY)
                        .anim("SLAB")
                        .renderable(910)
                        .scale(G.ZOOM)
                        .group(DILEMMA_GROUP)
                        .schedule(OperationFactory.tween(new Pos(x,y - insertDistanceY), new Pos(x,y), 1f, Interpolation.pow4Out ))
                        .build();

        int actorSlabOverlap = 6;
        int actorOffsetX = 16 * G.ZOOM;
        int actorOffsetY = (AssetSystem.SLAB_HEIGHT - actorSlabOverlap) * G.ZOOM;

        if (actorId != null) {
            Entity actor =
                    new DynastyEntityBuilder(world)
                            .pos(x + actorOffsetX, y + actorOffsetY- insertDistanceY)
                            .anim(actorId)
                            .renderable(908)
                            .scale(G.ZOOM)
                            .group(DILEMMA_GROUP)
                            .schedule(OperationFactory.tween(new Pos(x + actorOffsetX, y + actorOffsetY - insertDistanceY), new Pos(x + actorOffsetX, y + actorOffsetY), 1f, Interpolation.pow4Out ))
                            .build();

            int actorVsScrollMargin = 0;
            int scrollSlabOverlap = 11;
            int scrollOffsetX = (AssetSystem.DEFAULT_ACTOR_WIDTH + actorVsScrollMargin) * G.ZOOM + actorOffsetX;
            int scrollOffsetY = (AssetSystem.SLAB_HEIGHT - scrollSlabOverlap) * G.ZOOM;

            Entity scroll =
                    new DynastyEntityBuilder(world)
                            .pos(x + scrollOffsetX, y + scrollOffsetY- insertDistanceY)
                            .anim("SCROLL")
                            .renderable(912)
                            .scale(G.ZOOM)
                            .group(DILEMMA_GROUP)
                            .schedule(OperationFactory.tween(new Pos(x + scrollOffsetX, y + scrollOffsetY - insertDistanceY),
                                    new Pos(x + scrollOffsetX, y + scrollOffsetY), 1f, Interpolation.pow4Out ))
                            .build();

            int labelHeight = 8 * G.ZOOM;
            int labelMarginX = 12 * G.ZOOM;
            int labelMarginY = 8 * G.ZOOM;
            int labelOffsetY = scrollOffsetY + AssetSystem.SCROLL_HEIGHT * G.ZOOM;


            createLabel(x + scrollOffsetX + labelMarginX, y + labelOffsetY - labelMarginY, "3e2819", actorName, DILEMMA_SCROLL_SHADOW_COLOR, G.CANVAS_WIDTH - 10);
            createLabel(x + scrollOffsetX + labelMarginX, y + labelOffsetY - labelHeight * 1 - labelMarginY, "333333", actorRole, DILEMMA_SCROLL_SHADOW_COLOR, G.CANVAS_WIDTH - 10);
        }
    }

    /**
     * Remove active dilemma from screen.
     */
    private void stopDilemma() {
        EntityUtil.safeDeleteAll(groupManager.getEntities(DILEMMA_GROUP));
        dilemmaActive = false;
    }

    float noDisciplineCooldown = INITIAL_DISCIPLINE_WAIT_TIME;

    @Override
    protected void begin() {
        super.begin();

        if ( !dilemmaActive ) {
            noDisciplineCooldown -= world.delta;
            if (noDisciplineCooldown <= 0) {
                noDisciplineCooldown = DISCIPLINE_FOLLOWUP_WAIT_TIME;
                randomDilemma();
            }
        }
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
        int count = 0;
        while (dilemma == null && count++ < 1000) {
            dilemma = dilemmas.get(MathUtils.random(0, dilemmas.size() - 1));

            if (!predicatesMet(dilemma))
                dilemma = null;

            if (dilemma != null) {
                dilemma = startDilemma(dilemma);
                // if startdilemma fails, it returns NULL and we will search again.
            }
        }
    }

    /**
     * @return {@code true} if all predicates met (or no predicates. {@code false} if failed.
     */
    private boolean predicatesMet(Dilemma dilemma) {
        if (dilemma.predicates != null) {
            for (String predicate : dilemma.predicates) {
                switch (predicate) {
                    case "RIVER_DRY":
                        return riverSystem.getState() == RiverDioramaSystem.RiverState.RIVER_NONE;
                    case "RIVER_WATER":
                        return riverSystem.getState() == RiverDioramaSystem.RiverState.RIVER_WATER;
                    case "RIVER_BLOOD":
                        return riverSystem.getState() == RiverDioramaSystem.RiverState.RIVER_BLOOD;
                    case "NO_WIFE_PYRAMID":
                        return !structureSystem.hasWifePyramid();
                    case "OBELISK":
                        return structureSystem.hasObelisk();
                    default:
                        throw new RuntimeException("Missing predicate logic.");
                }
            }
        }
        return true;
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
     * @param success
     */
    public void ENDGAME(EndgameSystem.Success success) {
        stopDilemma();
        startDilemma(success.name() + "_DYNASTY");
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
            case "+ELEPHANT":
                stockpileSystem.alter(StockpileSystem.Resource.ELEPHANTS, 1);
                break;
            case "+CAMELS":
                stockpileSystem.alter(StockpileSystem.Resource.CAMELS, 1);
                break;
            case "+SOLDIERS":
                stockpileSystem.alter(StockpileSystem.Resource.SOLDIERS, 1);
                break;
            case "+COMPLETION":
                stockpileSystem.alter(StockpileSystem.Resource.COMPLETION, 1);
                break;
            case "PROGRESS":
                progressAlgorithmSystem.progress();
                break;
            case "+WORSHIP":
                stockpileSystem.alter(StockpileSystem.Resource.WORSHIP, 1);
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
            case "-ELEPHANTS":
                stockpileSystem.alter(StockpileSystem.Resource.ELEPHANTS, -1);
                break;
            case "-CAMELS":
                stockpileSystem.alter(StockpileSystem.Resource.CAMELS, -1);
                break;
            case "-SOLDIERS":
                stockpileSystem.alter(StockpileSystem.Resource.SOLDIERS, -1);
                break;
            case "-COMPLETION":
                stockpileSystem.alter(StockpileSystem.Resource.COMPLETION, -1);
                break;
            case "-WORSHIP":
                stockpileSystem.alter(StockpileSystem.Resource.WORSHIP, -1);
                break;

            case "+WIFE_PYRAMID":
                structureSystem.createWifePyramid();
                break;
            case "+OBELISK":
                structureSystem.createObelisk();
                break;
            case "-OBELISK":
                structureSystem.destroyObelisks();
                break;
            case "PYRAMID_SANDSTONE":
                structureSystem.pyramidDecor(StructureSystem.Decor.SANDSTONE);
                break;
            case "PYRAMID_MARBLE":
                structureSystem.pyramidDecor(StructureSystem.Decor.MARBLE);
                break;
            case "PYRAMID_GRANITE":
                structureSystem.pyramidDecor(StructureSystem.Decor.GRANITE);
                break;
            case "PYRAMID_PLAID":
                structureSystem.pyramidDecor(StructureSystem.Decor.PLAID);
                break;
            case "RIVER_BLOOD":
                riverSystem.blood();
                break;
            case "RIVER_DRY":
                riverSystem.clear();
                break;
            case "RIVER_WATER":
                riverSystem.water();
                break;
            case "NEGATIVE":
                startRandomDilemmaFromGroup("negative");
                break;
            case "POSITIVE":
                startRandomDilemmaFromGroup("positive");
                break;
            case "TEST":
                startRandomDilemmaFromGroup("test");
                break;
            case "TIMETRAVEL":
                endgameSystem.setFutureScene();
                break;
            case "FIREBALL":
                fireballSystem.queueFireball();
                break;
            case "SPACESHIP":
                fireballSystem.queueSpaceship();
                break;
            case "ROCK":
                fireballSystem.queueRock();
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
