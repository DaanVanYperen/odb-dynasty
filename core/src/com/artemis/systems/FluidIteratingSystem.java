package com.artemis.systems;

import com.artemis.Aspect;
import com.artemis.E;

import static com.artemis.E.E;

/**
 * @author Daan van Yperen
 */
public abstract class FluidIteratingSystem extends IteratingSystem {

    public FluidIteratingSystem(Aspect.Builder aspect) {
        super(aspect);
    }

    @Override
    protected void process(int id) {
        process(E(id));
    }

    protected abstract void process(E e);
}
