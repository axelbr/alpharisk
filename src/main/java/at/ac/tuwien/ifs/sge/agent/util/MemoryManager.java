package at.ac.tuwien.ifs.sge.agent.util;

import ai.djl.Device;
import ai.djl.ndarray.NDManager;

public class MemoryManager {
    private MemoryManager() {}
    private static Device device = Device.cpu();
    private static NDManager manager = NDManager.newBaseManager();

    public static NDManager getManager() {
        if (manager != null) {
            return manager;
        } else {
            manager = NDManager.newBaseManager(device);
            return manager;
        }
    }

    public static void setDevice(String device) {
        Device newDevice;
        if ("cpu".equals(device)) {
            newDevice = Device.cpu();
        } else if ("gpu".equals(device)) {
            newDevice = Device.gpu();
        } else {
            throw new IllegalArgumentException(String.format("Invalid device type \"%s\". Must be one of: cpu, gpu", device));
        }
        if (newDevice != MemoryManager.device) {
            MemoryManager.device = newDevice;
            manager.close();
            manager = null;
            manager = getManager();
        }
    }
}
