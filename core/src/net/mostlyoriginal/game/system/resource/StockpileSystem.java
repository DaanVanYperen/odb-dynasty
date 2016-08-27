package net.mostlyoriginal.game.system.resource;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.managers.TagManager;
import com.artemis.systems.EntityProcessingSystem;
import com.badlogic.gdx.math.MathUtils;
import net.mostlyoriginal.game.component.resource.Stockpile;

/**
 * Sync UI with Stockpile.
 *
 * @author Daan van Yperen
 */
public class StockpileSystem extends EntityProcessingSystem {

    protected ComponentMapper<Stockpile> mStockpile;
    private Stockpile Stockpile;
    private TagManager tagManager;

    public Stockpile getStockpile() {
        final Entity entity = tagManager.getEntity("dynasty");
        if (entity != null) {
            return mStockpile.get(entity);
        }
        return null;
    }

    public enum Resource {
        AGE,
        LIFESPAN,
        WEALTH,
        FOOD,
        WORKERS,
        COMPLETION
    }

    public StockpileSystem() {
        super(Aspect.all(net.mostlyoriginal.game.component.resource.Stockpile.class));
    }

    /**
     * inc/dec resource by amount.
     */
    public void alter(Resource resource, int amount) {
        final Stockpile stockpile = getStockpile();
        if (stockpile != null) {
            switch (resource) {
                case AGE:
                    stockpile.age = MathUtils.clamp(stockpile.age + amount, 0,100);
                    break;
                case LIFESPAN:
                    stockpile.lifespan = MathUtils.clamp(stockpile.lifespan + amount, 0,100);
                    break;
                case FOOD:
                    stockpile.food = MathUtils.clamp(stockpile.food + amount, 0,100);
                    break;
                case WORKERS:
                    stockpile.workers = MathUtils.clamp(stockpile.workers + amount, 0,100);
                    break;
                case WEALTH:
                    stockpile.wealth = MathUtils.clamp(stockpile.wealth + amount, 0,100);
                    break;
                case COMPLETION:
                    stockpile.completion = MathUtils.clamp(stockpile.completion + amount, 0,100);
                    break;
            }
        }
    }

    /**
     * get resource amount.
     */
    public int get(Resource resource) {
        final Stockpile stockpile = getStockpile();
        if (stockpile != null) {
            switch (resource) {
                case AGE:
                    return stockpile.age;
                case LIFESPAN:
                    return stockpile.lifespan;
                case FOOD:
                    return stockpile.food;
                case WORKERS:
                    return stockpile.workers;
                case WEALTH:
                    return stockpile.wealth;
                case COMPLETION:
                    return stockpile.completion;
            }
        }
        return 0;
    }

    @Override
    protected void initialize() {
        super.initialize();
    }

    @Override
    protected void process(Entity e) {
        Stockpile stockpile = mStockpile.get(e);
    }
}