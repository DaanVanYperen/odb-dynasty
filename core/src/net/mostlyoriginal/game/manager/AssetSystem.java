package net.mostlyoriginal.game.manager;

import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.ColorAnimation;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.manager.AbstractAssetSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.util.DynastyEntityBuilder;
import net.mostlyoriginal.api.util.GdxUtil;
import net.mostlyoriginal.game.G;
import net.mostlyoriginal.game.component.ui.Label;

/**
 * @author Daan van Yperen
 * @todo Split game logic and library logic.
 */
@Wire
public class AssetSystem extends AbstractAssetSystem {

    public static final int DANCING_MAN_WIDTH = 24;
    public static final int DANCING_MAN_HEIGHT = 36;
    public static final int PYRAMID_WIDTH = 160;
    public static final int PYRAMID_HEIGHT = 80;
    public static final int PYRAMID_WIFE_WIDTH = 55;
    public static final int PYRAMID_WIFE_HEIGHT = 28;
    public static final int OBELISK_WIDTH = 9;
    public static final int OBELISK_HEIGHT = 32 + 11;
    public static final int SUN_WIDTH = 48;
    public static final int SUN_HEIGHT = 48;
    public static final int SLAB_HEIGHT = 94;
    public static final int DEFAULT_ACTOR_WIDTH = 32;
    public static final int SCROLL_HEIGHT = 29;
    public static final int RIVER_HEIGHT = 17;
    public static final int SLAB_WIDTH = 147;
    public static final int LOGO_WIDTH = 139;
    public static final int LOGO_HEIGHT = 47;


    private TagManager tagManager;
    private M<Pos> mPos;

    public AssetSystem() {
        super("dynasty.png");

        add("SKY", 0, 0, 160, 133, 1);
        add("DESERT", 0, 133, 160, 133, 1);
        add("PYRAMID", 392, 0, PYRAMID_WIDTH, PYRAMID_HEIGHT, 1);

        add("PYRAMID-WIFE", 400, 216, PYRAMID_WIFE_WIDTH, PYRAMID_WIFE_HEIGHT, 1);

        add("SUN", 392, 152, SUN_WIDTH, SUN_HEIGHT, 1);

        add("WORKER", 416, 202, 2, 5, 1);
        add("ELEPHANT", 399, 200, 16, 9, 1);
        add("SOLDIER", 420, 202, 2, 5, 1);
        add("PHARAOH", 427, 200, 4, 7, 1);
        add("CAMEL", 434, 201, 12, 8, 1);

        add("SLAB", 400, 391, SLAB_WIDTH, SLAB_HEIGHT, 1);
        add("SCROLL", 548, 391, 89, SCROLL_HEIGHT, 1);

        add("STOCKPILE-TICK", 456, 236, 12, 10, 1);

        add("RIVER", 400, 272, 160, RIVER_HEIGHT, 1, 4);
        add("RIVER-BLOOD", 561, 272, 160, RIVER_HEIGHT, 1, 4);

        add("OBELISK BASE 1", 352, 212 - 11, OBELISK_WIDTH, OBELISK_HEIGHT, 1);
        add("OBELISK BASE 2", 361, 212 - 11, OBELISK_WIDTH, OBELISK_HEIGHT, 1);
        add("OBELISK BASE 3", 370, 212 - 11, OBELISK_WIDTH, OBELISK_HEIGHT, 1);
        add("OBELISK BASE 4", 379, 212 - 11, OBELISK_WIDTH, OBELISK_HEIGHT, 1);
        add("OBELISK BASE 5", 388, 212 - 11, OBELISK_WIDTH, OBELISK_HEIGHT, 1);

        add("PROGRESS BAR BACKGROUND", 400, 490, 147, 16, 1);
        add("PROGRESS BAR", 552, 495, 8, 7, 1);
        add("PROGRESS BAR PLANNED", 560, 495, 8, 7, 1);

        add("SCAFFOLDING BIG 1", 464, 216, 16, 16, 1);
        add("SCAFFOLDING BIG 2", 464, 232, 16, 16, 1);
        add("SCAFFOLDING BIG 3", 480, 232, 16, 16, 1);
        add("SCAFFOLDING SMALL TOP 1", 480, 216, 8, 8, 1);
        add("SCAFFOLDING SMALL TOP 2", 480, 224, 8, 8, 1);
        add("SCAFFOLDING SMALL TOP 3", 488, 216, 8, 8, 1);
        add("SCAFFOLDING SMALL TILE 1", 488, 224, 8, 8, 1);
        add("SCAFFOLDING BACKGROUND SMALL 1", 496, 224, 8, 8, 1);
        add("SCAFFOLDING FLAG", 496, 216, 8, 8, 1);
        add("SCAFFOLDING OBJECTS 1", 504, 216, 8, 8, 1);
        add("SCAFFOLDING OBJECTS 2", 512, 216, 8, 8, 1);
        add("SCAFFOLDING OBJECTS 3", 520, 216, 8, 8, 1);
        add("SCAFFOLDING OBJECTS 4", 528, 216, 8, 8, 1);
        add("SCAFFOLDING OBJECTS 5", 536, 216, 8, 8, 1);
        add("SCAFFOLDING OBJECTS 6", 504, 224, 8, 8, 1);
        add("SCAFFOLDING OBJECTS 7", 512, 224, 8, 8, 1);
        add("SCAFFOLDING OBJECTS 8", 520, 224, 8, 8, 1);
        add("SCAFFOLDING OBJECTS 9", 528, 224, 8, 8, 1);
        add("SCAFFOLDING OBJECTS 10", 536, 224, 8, 8, 1);


        add("PYRAMID Granite", 712, 0, 160, 133, 1);

        add("ALIEN", 400, 344, 22, 39, 4);

        add("PRIEST", 488, 344, 28, 38, 4);

        add("WIFE", 600, 344, 27, 37, 4);

        add("GUIDE", 708, 344, 30, 38, 4);

        add("LOGO", 170, 36, LOGO_WIDTH, LOGO_HEIGHT, 1);



        /*
        WAVES: 160, 129, 160, 4
        FLOOD: 160, 133, 160, 133

        DUST: 440, 160, 3,  3



GROUND BLOCKS: x:464, y:253, width:12,  height:10
GROUND BLOCKS: x:476, y:250, width:13,  height:13
GROUND BLOCKS: x:489, y:255, width:11 ,  height:8
GROUND BLOCKS: x:500, y:248, width:13,  height:15

WARELEPHANT: x:399, y:200, width:16,  height:9

PYRAMID SANDSTONE:
PYRAMID MARBLE: x:552, y:0, width:160, height:133

PROGRESS BAR BACKGROUND: x:400, y:490, width:147, height:16
PROGRESS BAR GREEN: x:552, y:495, width:8, height:7
PROGRESS BAR GREY: x:560, y:495, width:8, height:7
        */

        add("btn-test-up", 400, 160 + 40, 16, 10, 1);
        add("btn-test-hover", 400, 160 + 40, 16, 10, 1);
        add("btn-test-down", 400, 160 + 40, 16, 10, 1);

        add("dust_particle", 32, 136, 4, 4, 1);

        //
        add("dancingman", 32, 56, 24, 56, 1);
        add("cursor", 1, 1, 1, 1, 1);

        add("progress-bubble-0", 56, 112, 8, 8, 1);
        add("progress-bubble-1", 56, 120, 8, 8, 1);
        add("progress-bar-0", 64, 112, 8, 8, 1);
        add("progress-bar-1", 64, 120, 8, 8, 1);
        add("progress-indicator", 56, 96, 8, 11, 1);

        add("gate", 32, 56, 24, 56, 1);

        add("state-dead", 120, 64, 8, 8, 1);
        add("state-slug", 104, 72, 8, 8, 1);

        add("btn-engage-up", 171, 85, 56, 20, 1);
        add("btn-engage-hover", 171, 84, 56, 20, 1);
        add("btn-engage-down", 171, 81, 56, 20, 1);

        add("bar-fuel", 48, 177, 8, 6, 1);
        add("bar-food", 48, 185, 8, 6, 1);
        add("bar-biogel", 48, 169, 8, 6, 1);
        //add("bar-crew", 48,160, 8, 8, 1);

        add("bar-fuel-open", 48 + 8, 177, 8, 6, 1);
        add("bar-food-open", 48 + 8, 185, 8, 6, 1);
        add("bar-biogel-open", 48 + 8, 169, 8, 6, 1);
        //add("bar-crew-open", 48+8,160, 8, 8, 1);

        add("pickup-fuel", 48, 177, 8, 6, 1);
        add("pickup-food", 48, 185, 8, 6, 1);
        add("pickup-biogel", 48, 169, 8, 6, 1);
        add("pickup-crew", 48, 160, 8, 8, 1);

        add("crew-0", 104, 64, 8, 8, 1);
        add("crew-1", 104 - 8, 64, 8, 8, 1);
        add("crew-2", 104 - 8, 64 + 8, 8, 8, 1);

        add("btn-construct-up", 173, 109, 17, 17, 1);
        add("btn-construct-hover", 196, 108, 17, 17, 1);
        add("btn-construct-down", 171, 107, 17, 17, 1);

        add("btn-scan-up", 165, 181, 31, 17, 1);
        add("btn-scan-hover", 165, 180, 31, 17, 1);
        add("btn-scan-down", 165, 178, 31, 17, 1);

        add("btn-heal-up", 81, 169, 6, 8, 1);
        add("btn-heal-hover", 81, 168, 6, 8, 1);
        add("btn-heal-down", 81, 167, 6, 8, 1);

        add("c-indicator", 112, 72, 8, 8, 1);

        add("c-bunks", 152, 24, 8, 8, 1);
        add("c-medbay", 144, 16, 8, 8, 1);
        add("c-hydroponics", 136, 40, 8, 8, 1);
        add("c-storagepod", 144, 32, 8, 8, 1);
        add("c-engine", 80, 24, 8, 8, 1);
        add("c-ramscoop", 112 + 8, 56, 8, 8, 1);
        add("c-chain", 64, 40, 8, 8, 1);

        add("c-bunks-placed", 152, 24, 8, 8, 1);
        add("c-medbay-placed", 144, 16, 8, 8, 1);
        add("c-hydroponics-placed", 136, 40, 8, 8, 1);
        add("c-storagepod-placed", 144, 32, 8, 8, 1);
        add("c-engine-placed", 80, 24, 16, 8, 1);
        add("c-ramscoop-placed", 112, 56, 16, 8, 1);
        add("c-chain-placed", 64, 40, 8, 8, 1);

        add("c-bunks-building", 184, 24, 8, 8, 1);
        add("c-medbay-building", 176, 16, 8, 8, 1);
        add("c-hydroponics-building", 168, 40, 8, 8, 1);
        add("c-storagepod-building", 176, 32, 8, 8, 1);
        add("c-engine-building", 176, 40, 16, 8, 1);
        add("c-ramscoop-building", 176, 48, 16, 8, 1);
        add("c-chain-building", 64, 40, 8, 8, 1);

        add("hull-0", 88, 32, 8, 8, 1); // top left
        add("hull-1", 96, 32, 8, 8, 1); // top
        add("hull-1-solar", 112, 32, 8, 8, 1); // top
        add("hull-1-wing", 104, 24, 8, 16, 1); // top
        add("hull-2", 120, 32, 8, 8, 1); // top right
        add("hull-3", 88, 40, 8, 8, 1); // left
        add("hull-4", 120, 40, 8, 8, 1); // right
        add("hull-5", 88, 48, 8, 8, 1); // bottom left
        add("hull-6", 96, 48, 8, 8, 1); // bottom
        add("hull-7", 120, 48, 8, 8, 1); // bottom right
        add("hull-inny-0", 112, 16, 8, 8, 1); // bottom-right-inny.
        add("hull-inny-1", 120, 16, 8, 8, 1); // bottom-left-inny
        add("hull-inny-2", 112, 24, 8, 8, 1); // top-right-inny
        add("hull-inny-3", 120, 24, 8, 8, 1); // top-left-inny
        add("hull-missing", 96, 40, 8, 8, 1); // missing

        add("hull-0-building", 128, 88, 8, 8, 1); // top left
        add("hull-1-building", 136, 88, 8, 8, 1); // top
        add("hull-1-solar-building", 152, 88, 8, 8, 1); // yop
        add("hull-1-wing-building", 144, 80, 8, 16, 1); // yop
        add("hull-2-building", 160, 88, 8, 8, 1); // top right
        add("hull-3-building", 128, 96, 8, 8, 1); // left
        add("hull-4-building", 160, 96, 8, 8, 1); // right
        add("hull-5-building", 128, 104, 8, 8, 1); // bottom left
        add("hull-6-building", 136, 104, 8, 8, 1); // bottom
        add("hull-7-building", 160, 104, 8, 8, 1); // bottom right
        add("hull-inny-0-building", 144, 64, 8, 8, 1); // bottom-right-inny.
        add("hull-inny-1-building", 152, 64, 8, 8, 1); // bottom-left-inny
        add("hull-inny-2-building", 144, 70, 8, 8, 1); // top-right-inny
        add("hull-inny-3-building", 152, 70, 8, 8, 1); // top-left-inny
        add("hull-missing-building", 96, 40, 8, 8, 1); // missing

        final Animation arrow = add("arrow", 136, 56, 8, 8, 3);// missing
        arrow.setFrameDuration(1 / 15f);

        add("star-0-0", 32, 136, 4, 4, 1);
        add("star-0-1", 32 - 4, 136, 4, 4, 1);
        add("star-0-2", 32 - 8, 136, 4, 4, 1);
        add("star-0-3", 40, 136, 7, 4, 1);
        add("star-0-4", 48, 136, 26, 4, 1);
        add("star-0-5", 80, 136, 36, 4, 1);

        add("star-1-0", 32, 144, 2, 2, 1);
        add("star-1-1", 32 - 4, 144, 2, 2, 1);
        add("star-1-2", 32 - 8, 144, 2, 2, 1);
        add("star-1-3", 40, 144, 6, 2, 1);
        add("star-1-4", 48, 144, 12, 2, 1);
        add("star-1-5", 80, 144, 21, 2, 1);

        add("star-2-0", 32, 152, 1, 1, 1);
        add("star-2-1", 32 - 4, 152, 1, 1, 1);
        add("star-2-2", 32 - 8, 152, 1, 1, 1);
        add("star-2-3", 40, 152, 4, 1, 1);
        add("star-2-4", 48, 152, 7, 1, 1);
        add("star-2-5", 80, 152, 15, 1, 1);

        add("engine-0", 64, 16, 16, 8, 1);
        add("engine-1", 64, 16 + 8, 16, 8, 1);
        add("engine-2", 64, 16 + 16, 16, 8, 1);

        loadSounds(new String[]{
                "catapult",
                "catapult_impact",
                "chisel_strike1",
                "chisel_strike2",
                "chisel_strike3",
                "chisel_strike4",
                "hammer_strike1",
                "hammer_strike2",
                "hammer_strike3",
                "pyramid_rise", //
                "workers_cheering" //
        });

        Music music = Gdx.audio.newMusic(Gdx.files.internal("sfx/music.mp3"));
        music.setLooping(true);
        music.play();
        music.setPan(0, 0.1f);
    }

    public void playRandomHammer() {
        String[] sfx = {"hammer_strike1",
                "hammer_strike2",
                "hammer_strike3"};
        playSfx(sfx[MathUtils.random(0, sfx.length - 1)]);
    }

    public void playRandomChisel() {
        String[] sfx = {"chisel_strike1",
                "chisel_strike2",
                "chisel_strike3",
                "chisel_strike4"};
        playSfx(sfx[MathUtils.random(0, sfx.length - 1)]);
    }

    @Override
    protected void initialize() {
        super.initialize();
        final Label label = new Label(G.version);
        label.align = Label.Align.RIGHT;
        Entity entity = new DynastyEntityBuilder(world)
                .with(Pos.class, Renderable.class)
                .with(label, new ColorAnimation(GdxUtil.convert(Color.WHITE), GdxUtil.convert(Color.valueOf("333333")), GdxUtil.convert(Interpolation.exp5), 1f / 2f, 2f)).build();
        mPos.get(entity).xy.set(G.CANVAS_WIDTH - 2, G.CANVAS_HEIGHT - 2);
    }

}
