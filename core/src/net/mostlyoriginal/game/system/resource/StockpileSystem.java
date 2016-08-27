package net.mostlyoriginal.game.system.resource;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.managers.TagManager;
import com.artemis.systems.EntityProcessingSystem;
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
                    stockpile.age = stockpile.age + amount;
                    break;
                case LIFESPAN:
                    stockpile.lifespan = stockpile.lifespan + amount;
                    break;
                case FOOD:
                    stockpile.food = stockpile.food + amount;
                    break;
                case WORKERS:
                    stockpile.workers = stockpile.workers + amount;
                    break;
                case WEALTH:
                    stockpile.wealth = stockpile.wealth + amount;
                    break;
                case COMPLETION:
                    stockpile.completion = stockpile.completion + amount;
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