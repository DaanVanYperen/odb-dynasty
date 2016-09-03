package net.mostlyoriginal.api.util;

import com.artemis.*;
import com.artemis.managers.GroupManager;
import com.artemis.managers.PlayerManager;
import com.artemis.managers.TagManager;
import com.artemis.managers.UuidEntityManager;
import com.artemis.utils.reflect.ClassReflection;
import com.artemis.utils.reflect.ReflectionUtil;
import com.badlogic.gdx.graphics.Color;
import net.mostlyoriginal.api.component.Schedule;
import net.mostlyoriginal.api.component.basic.Bounds;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.basic.Scale;
import net.mostlyoriginal.api.component.common.ExtendedComponent;
import net.mostlyoriginal.api.component.common.Mirrorable;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Invisible;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.component.physics.Gravity;
import net.mostlyoriginal.api.component.physics.Physics;
import net.mostlyoriginal.api.operation.common.Operation;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.game.component.agent.Ancient;
import net.mostlyoriginal.game.component.resource.Fireball;
import net.mostlyoriginal.game.component.resource.Minion;
import net.mostlyoriginal.game.component.resource.ZPos;
import net.mostlyoriginal.game.component.ui.*;

import java.util.UUID;

/**
 * Created by Daan on 28-8-2016.
 */
public class B {

    // Start duplication

    private final World world;
    private final int entity;

    /**
     * Begin building new entity.
     */
    public B(World world) {
        this.world = world;
        entity = world.create();
    }

    /**
     * Begin building new entity based on archetype.
     */
    public B(World world, Archetype archetype) {
        this.world = world;
        entity = world.create(archetype);
    }

    /**
     * Add artemis managed components to entity.
     */
    public B mirror(ExtendedComponent component) {
        M.getFor(component,world).create(entity).set(component);
        return this;
    }

    /**
     * Add unmanaged components to entity. Use sparingly!
     */
    public B withUnpooled(Component component) {
        world.getEntity(entity).edit().add(component);
        return this;
    }

    /**
     * Add artemis managed components to entity.
     */
    public B with(Class<? extends Component> component) {
        create(component);
        return this;
    }

    /**
     * Add artemis managed components to entity.
     */
    public B with(Class<? extends Component> component1, Class<? extends Component> component2) {
        create(component1);
        create(component2);
        return this;
    }

    /**
     * Add artemis managed components to entity.
     */
    public B with(Class<? extends Component> component1, Class<? extends Component> component2, Class<? extends Component> component3) {
        create(component1);
        create(component2);
        create(component3);
        return this;
    }

    /**
     * Add artemis managed components to entity.
     */
    public B with(Class<? extends Component> component1, Class<? extends Component> component2, Class<? extends Component> component3, Class<? extends Component> component4) {
        create(component1);
        create(component2);
        create(component3);
        create(component4);
        return this;
    }

    /**
     * Add artemis managed components to entity.
     */
    public B with(Class<? extends Component> component1, Class<? extends Component> component2, Class<? extends Component> component3, Class<? extends Component> component4, Class<? extends Component> component5) {
        create(component1);
        create(component2);
        create(component3);
        create(component4);
        create(component5);
        return this;
    }

    /**
     * Add artemis managed components to entity.
     */
    public B with(Class<? extends Component>... components) {
        for (int i = 0, n = components.length; i < n; i++) {
            create(components[i]);
        }
        return this;
    }

    /**
     * Register entity with tag. Requires registered TagManager
     */
    public B tag(String tag) {
        resolveManager(TagManager.class).register(tag, entity);
        return this;
    }

    /**
     * Register entity with group. Requires registered TagManager
     */
    public B group(String group) {
        resolveManager(GroupManager.class).add(world.getEntity(entity), group);
        return this;
    }

    /**
     * Register entity with multiple groups. Requires registered TagManager
     */
    public B groups(String... groups) {
        for (int i = 0; groups.length > i; i++) {
            group(groups[i]);
        }
        return this;
    }

    /**
     * Assemble, add to world
     */
    public Entity build() {
        return world.getEntity(entity);
    }

    /**
     * Assemble, add to world
     */
    public int id() {
        return entity;
    }

    /**
     * Fetch manager or throw RuntimeException if not registered.
     */
    protected <T extends BaseSystem> T resolveManager(Class<T> type) {
        final T teamManager = world.getSystem(type);
        if (teamManager == null) {
            throw new RuntimeException("Register " + ClassReflection.getSimpleName(type) + " with your artemis world.");
        }
        return teamManager;
    }

    // End duplication.

    public B pos(float x, float y) {
        create(Pos.class).xy.set(x, y);
        return this;
    }

    public B z(float z) {
        create(ZPos.class).z = z;
        return this;
    }

    public B renderable(int layer) {
        create(Renderable.class).layer = layer;
        return this;
    }

    public B anim(String id) {
        create(Anim.class).id = id;
        return this;
    }

    public B anim() {
        create(Anim.class);
        return this;
    }

    public B scale(float scale) {
        create(Scale.class).scale = scale;
        return this;
    }

    public B tint(Color color) {
        create(Tint.class).set(color);
        return this;
    }

    public B tint(String color) {
        create(Tint.class).setHex(color);
        return this;
    }

    public B script(Operation operation) {
        create(Schedule.class).operation.add(operation);
        return this;
    }

    public B minion(int productivity, String deathSfx) {
        Minion minion = create(Minion.class);
        minion.productivity = productivity;
        minion.deathSfx = deathSfx;
        return this;
    }

    public B velocity(float vx, float vy, float friction) {
        Physics velocity = create(Physics.class);
        velocity.vx = vx;
        velocity.vy = vy;
        velocity.friction = friction;
        return this;
    }

    public B gravity(float x, float y) {
        Gravity gravity = create(Gravity.class);
        gravity.x = x;
        gravity.y = y;
        return this;
    }

    public B fireball(boolean smoke, boolean spark, boolean explodes) {
        Fireball fireball = create(Fireball.class);
        fireball.hasSmoke = smoke;
        fireball.hasSparks = spark;
        fireball.explodes = explodes;
        return this;
    }

    public B ancient() {
        create(Ancient.class);
        return this;
    }

    public B invisible() {
        create(Invisible.class);
        return this;
    }

    public B bounds(final int minx, final int miny, final int maxx, final int maxy) {
        Bounds bounds = create(Bounds.class);
        bounds.set(minx, miny, maxx, maxy);
        return this;
    }

    private <T extends Component> T create(Class<T> clazz) {
        return M.getFor(clazz, world).create(entity);
    }

    public B clickable() {
        create(Clickable.class);
        return this;
    }

    public B button(String animPrefix, ButtonListener listener, String hint) {
        Button button = create(Button.class);
        button.set(animPrefix, listener, hint);
        return this;
    }

    public B button(String animHover, String animClicked, String animDefault, ButtonListener listener) {
        Button button = create(Button.class);
        button.animHover = animHover;
        button.animClicked = animClicked;
        button.animDefault = animDefault;
        button.listener = listener;
        return this;
    }

    public B label(String text, Label.Align align) {
        Label label = create(Label.class);
        label.text = text;
        label.align = align;
        return this;
    }

    public B label(String text) {
        Label label = create(Label.class);
        label.text = text;
        return this;
    }

    public B pos() {
        create(Pos.class);
        return this;
    }

    public B renderable() {
        create(Renderable.class);
        return this;
    }

    public B tint() {
        create(Tint.class);
        return this;
    }
}
