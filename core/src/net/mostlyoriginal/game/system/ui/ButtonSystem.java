package net.mostlyoriginal.game.system.ui;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.graphics.Color;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Invisible;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.util.GdxUtil;
import net.mostlyoriginal.game.component.ui.Button;
import net.mostlyoriginal.game.component.ui.Clickable;
import net.mostlyoriginal.game.component.ui.Label;
import net.mostlyoriginal.game.manager.AssetSystem;

import static com.artemis.E.E;

/**
 * @author Daan van Yperen
 */
@Wire
public class ButtonSystem extends EntityProcessingSystem {

    public static final float COOLDOWN_AFTER_BUTTON_CLICK = 0.15f;
    public static final int NO_ENTITY = -1;
    protected ComponentMapper<Clickable> mClickable;

    protected ComponentMapper<Label> mLabel;
    protected ComponentMapper<Button> mButton;
    protected ComponentMapper<Anim> mAnim;
    protected ComponentMapper<Tint> mColor;
    protected ComponentMapper<Invisible> mInvisible;
    protected M<Pos> mPos;
    public Label hintlabel;
    public float globalButtonCooldown = 0;
    private AssetSystem assetSystem;

    public ButtonSystem() {
        super(Aspect.all(Button.class, Clickable.class, Bounds.class).one(Anim.class, Label.class));
    }

    @Override
    protected void initialize() {
        super.initialize();

        Entity hint = E()
                .pos(10,6).renderable().tintHex("004290")
                .localLabel("hintlabel").entity();

        hintlabel = mLabel.get(hint);
    }

    @Override
    protected void process(Entity e) {
        updateAnim(e);
    }

    @Override
    protected void begin() {
        super.begin();
        hintlabel.text = null;
        globalButtonCooldown -= world.delta;
    }

    private void updateAnim(Entity e) {
        String id = getNewAnimId(e);

        if (id != null) {
            final Button button = mButton.get(e);
            boolean automaticDisable = button.hideIfDisabled && !button.listener.enabled();
            if (automaticDisable) {
                id = null;
            }

            // quick hack to hide icons when button is hidden. @todo cleanup.
            if (button.transientIcon != NO_ENTITY) {
                int bute = button.transientIcon;
                if ((id != null) && mInvisible.has(bute)) {
                    mInvisible.remove(bute);
                }
                if ((id == null) && !mInvisible.has(bute)) {
                    mInvisible.create(bute);
                }
            }

            if (mAnim.has(e)) {
                mAnim.get(e).id = id;
            } else if (mColor.has(e)) {
                // @todo fix this hack! XD
                mColor.get(e).set(GdxUtil.asColor(id));
            }
        }
    }

    private String getNewAnimId(Entity e) {
        final Clickable clickable = mClickable.get(e);
        final Button button = mButton.get(e);
        if (button.autoclick) {
            button.autoclickCooldown -= world.delta;
        }

        // disable the button temporarily after use to avoid trouble.
        if (button.cooldown >= 0) {
            button.cooldown -= world.delta;
            return button.animClicked;
        }

        // gray out disabled items. @todo separate.
        boolean active = button.listener.enabled() && !button.manualDisable;
        if (mColor.has(e)) {
            Color color = mColor.get(e).color;
            color.r = button.color.r * (active ? 1f : 0.5f);
            color.g = button.color.g * (active ? 1f : 0.5f);
            color.b = button.color.b * (active ? 1f : 0.5f);
            color.a = button.color.a;

            if (button.transientIcon != NO_ENTITY) {
                final int iconEntity = button.transientIcon;
                if (mColor.has(iconEntity)) {
                    mColor.get(iconEntity).set(color);
                }
            }

            if (!active) {
                return button.animDefault;
            }
        }

        switch (clickable.state) {
            case HOVER:
                if (button.autoclick && button.autoclickCooldown <= 0) {
                    return click(button);
                }
                hintlabel.text = button.hint;
                return button.animHover;
            case CLICKED:
                if (!button.autoclick) return click(button);
            default:
                return button.animDefault;
        }
    }

    private String click(Button button) {
        button.cooldown = COOLDOWN_AFTER_BUTTON_CLICK;
        triggerButton(button);
        return button.animClicked;
    }

    private void triggerButton(Button button) {
        if (button.listener.enabled() && globalButtonCooldown <= 0 && !button.manualDisable) {

            if (!button.autoclick) assetSystem.playSfx("snd-click");
            // prevent spamming by accident.
            if (!button.autoclick) globalButtonCooldown = 0.1f;
            button.listener.run();
        }
    }
}
