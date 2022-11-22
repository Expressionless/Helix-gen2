package io.sly.helix.game.entities;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class HelixEntityManager {
    
    private List<HelixEntity> addBuffer = new ArrayList<>();
    private List<HelixEntity> removeBuffer = new ArrayList<>();
	public List<HelixEntity> entities = new ArrayList<>();

    public HelixEntityManager() {
    }

    public void render(SpriteBatch sb) {
		for(HelixEntity entity : entities) {
            if(entity.isActive())
                entity.render(sb);
        }
    }

    public void update(float delta) {
        addEntities();
        updateEntities(delta);
        removeEntities();
    }

    public void add(HelixEntity e) {
        addBuffer.add(e);
    }

    public void remove(HelixEntity e) {
        this.destroy(e);
    }

    public void destroy(HelixEntity e) {
        // e.setActive(false);
        e.dispose();
        removeBuffer.add(e);
    }

    private void addEntities() {
        while(!addBuffer.isEmpty()) {
            HelixEntity e = addBuffer.get(0);
            // e.setActive(true);
            if(e != null)
                entities.add(e);
            addBuffer.remove(0);
        }
    }

    private void removeEntities() {
        while(!removeBuffer.isEmpty()) {
            entities.remove(removeBuffer.get(0));
            removeBuffer.remove(0);
        }
    }

    private void updateEntities(float delta) {
		for(HelixEntity entity : entities) {
            if(entity.isActive())
                entity.update(delta);
        }
    }
}
