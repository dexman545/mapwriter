package dex.mapwriter3.tasks;

import dex.mapwriter3.region.RegionManager;

public class CloseRegionManagerTask extends Task {

    private final RegionManager regionManager;

    public CloseRegionManagerTask(RegionManager regionManager) {
        this.regionManager = regionManager;
    }

    @Override
    public void run() {
        this.regionManager.close();
    }

    @Override
    public void onComplete() {
    }

    @Override
    public boolean CheckForDuplicate() {
        return false;
    }
}
