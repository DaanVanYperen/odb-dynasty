package net.mostlyoriginal.api.util;

import com.artemis.*;
import com.artemis.managers.GroupManager;
import com.artemis.managers.PlayerManager;
import com.artemis.managers.TagManager;
import com.artemis.managers.UuidEntityManager;
import com.artemis.utils.reflect.ClassReflection;
import com.badlogic.gdx.graphics.Color;
import net.mostlyoriginal.api.component.Schedule;
import net.mostlyoriginal.api.component.basic.Pos;
import net.mostlyoriginal.api.component.basic.Scale;
import net.mostlyoriginal.api.component.graphics.Anim;
import net.mostlyoriginal.api.component.graphics.Renderable;
import net.mostlyoriginal.api.component.graphics.Tint;
import net.mostlyoriginal.api.component.physics.Gravity;
import net.mostlyoriginal.api.component.physics.Physics;
import net.mostlyoriginal.api.operation.common.Operation;
import net.mostlyoriginal.game.component.resource.Fireball;
import net.mostlyoriginal.game.component.resource.Minion;
import net.mostlyoriginal.game.component.resource.ZPos;

import java.util.UUID;

/**
 * Created by Daan on 28-8-2016.
 */
public class DynastyEntityBuilder {

    // Start duplication

    private final World world;
    private final EntityEdit edit;

    /**
     * Begin building new entity.
     */
    public DynastyEntityBuilder(World world) {
        this.world = world;
        edit = world.createEntity().edit();
    }

    /**
     * Begin building new entity based on archetype.
     */
    public DynastyEntityBuilder(World world, Archetype archetype) {
        this.world = world;
        edit = world.createEntity(archetype).edit();
    }

    /**
     * Add component to entity.
     */
    public DynastyEntityBuilder with(Component component) {
        edit.add(component);
        return this;
    }

    /**
     * Add components to entity.
     */
    public DynastyEntityBuilder with(Component component1, Component component2) {
        edit.add(component1);
        edit.add(component2);
        return this;
    }

    /**
     * Add components to entity.
     */
    public DynastyEntityBuilder with(Component component1, Component component2, Component component3) {
        edit.add(component1);
        edit.add(component2);
        edit.add(component3);
        return this;
    }

    /**
     * Add components to entity.
     */
    public DynastyEntityBuilder with(Component component1, Component component2, Component component3, Component component4) {
        edit.add(component1);
        edit.add(component2);
        edit.add(component3);
        edit.add(component4);
        return this;
    }

    /**
     * Add components to entity.
     */
    public DynastyEntityBuilder with(Component component1, Component component2, Component component3, Component component4, Component component5) {
        edit.add(component1);
        edit.add(component2);
        edit.add(component3);
        edit.add(component4);
        edit.add(component5);
        return this;
    }

    /**
     * Add components to entity.
     */
    public DynastyEntityBuilder with(Component... components) {
        for (int i = 0, n = components.length; i < n; i++) {
            edit.add(components[i]);
        }
        return this;
    }

    /**
     * Add artemis managed components to entity.
     */
    public DynastyEntityBuilder with(Class<? extends Component> component) {
        edit.create(component);
        return this;
    }

    /**
     * Add artemis managed components to entity.
     */
    public DynastyEntityBuilder with(Class<? extends Component> component1, Class<? extends Component> component2) {
        edit.create(component1);
        edit.create(component2);
        return this;
    }

    /**
     * Add artemis managed components to entity.
     */
    public DynastyEntityBuilder with(Class<? extends Component> component1, Class<? extends Component> component2, Class<? extends Component> component3) {
        edit.create(component1);
        edit.create(component2);
        edit.create(component3);
        return this;
    }

    /**
     * Add artemis managed components to entity.
     */
    public DynastyEntityBuilder with(Class<? extends Component> component1, Class<? extends Component> component2, Class<? extends Component> component3, Class<? extends Component> component4) {
        edit.create(component1);
        edit.create(component2);
        edit.create(component3);
        edit.create(component4);
        return this;
    }

    /**
     * Add artemis managed components to entity.
     */
    public DynastyEntityBuilder with(Class<? extends Component> component1, Class<? extends Component> component2, Class<? extends Component> component3, Class<? extends Component> component4, Class<? extends Component> component5) {
        edit.create(component1);
        edit.create(component2);
        edit.create(component3);
        edit.create(component4);
        edit.create(component5);
        return this;
    }

    /**
     * Add artemis managed components to entity.
     */
    public DynastyEntityBuilder with(Class<? extends Component>... components) {
        for (int i = 0, n = components.length; i < n; i++) {
            edit.create(components[i]);
        }
        return this;
    }

    /**
     * Set UUID of entity
     */
    public DynastyEntityBuilder UUID(UUID uuid) {
        resolveManager(UuidEntityManager.class).setUuid(edit.getEntity(), uuid);
        return this;
    }

    /**
     * Register entity with owning player.
     * An entity can only belong to a single player at a time.
     * Requires registered PlayerManager.
     */
    public DynastyEntityBuilder player(String player) {
        resolveManager(PlayerManager.class).setPlayer(edit.getEntity(), player);
        return this;
    }

    /**
     * Register entity with tag. Requires registered TagManager
     */
    public DynastyEntityBuilder tag(String tag) {
        resolveManager(TagManager.class).register(tag, edit.getEntity());
        return this;
    }

    /**
     * Register entity with group. Requires registered TagManager
     */
    public DynastyEntityBuilder group(String group) {
        resolveManager(GroupManager.class).add(edit.getEntity(), group);
        return this;
    }

    /**
     * Register entity with multiple groups. Requires registered TagManager
     */
    public DynastyEntityBuilder groups(String... groups) {
        for (int i = 0; groups.length > i; i++) {
            group(groups[i]);
        }
        return this;
    }

    /**
     * Assemble, add to world
     */
    public Entity build() {
        return edit.getEntity();
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

    public DynastyEntityBuilder pos(float x, float y) {
        edit.create(Pos.class).xy.set(x, y);
        return this;
    }

    public DynastyEntityBuilder z(float z) {
        edit.create(ZPos.class).z = z;
        return this;
    }

    public DynastyEntityBuilder renderable(int layer) {
        edit.create(Renderable.class).layer = layer;
        return this;
    }

    public DynastyEntityBuilder anim(String id) {
        edit.create(Anim.class).id = id;
        return this;
    }

    public DynastyEntityBuilder scale(float scale) {
        edit.create(Scale.class).scale = scale;
        return this;
    }

    public DynastyEntityBuilder tint(Color color) {
        edit.create(Tint.class).set(color);
        return this;
    }

    public DynastyEntityBuilder tint(String color) {
        edit.create(Tint.class).setHex(color);
        return this;
    }

    public DynastyEntityBuilder schedule(Operation operation) {
        edit.create(Schedule.class).operation.add(operation);
        return this;
    }

    public DynastyEntityBuilder minion(int productivity) {
        edit.create(Minion.class).productivity = productivity;
        return this;
    }

    public DynastyEntityBuilder velocity(float vx, float vy, float friction) {
        Physics velocity = edit.create(Physics.class);
        velocity.vx = vx;
        velocity.vy = vy;
        velocity.friction = friction;
        return this;
    }

    public DynastyEntityBuilder gravity(float x, float y) {
        Gravity gravity = edit.create(Gravity.class);
        gravity.x=x;
        gravity.y=y;
        return this;
    }

    public DynastyEntityBuilder fireball(boolean smoke, boolean spark, boolean explodes) {
        Fireball fireball = edit.create(Fireball.class);
        fireball.hasSmoke= smoke;
        fireball.hasSparks= spark;
        fireball.explodes=explodes;
        return this;
    }
}
