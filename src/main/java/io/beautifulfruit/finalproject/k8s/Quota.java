package io.beautifulfruit.finalproject.k8s;

public class Quota {
    /**
     * Memeory limit in MB
     */
    public int memory = 0;
    /**
     * CPU limit in 0.1 cores
     */
    public int cpu = 0;
    /**
     * Disk limit in MB
     */
    public int disk = 0;

    public Quota() {
    }

    public Quota(int memory, int cpu, int disk) {
        this.memory = memory;
        this.cpu = cpu;
        this.disk = disk;
    }

    public static Quota defaultUserQuota() {
        return new Quota(8192, 160, 8192);
    }

    public static Quota defaultDeploymentQuota() {
        return new Quota(2048, 40, 2048);
    }

    public Quota increaseBy(Quota q) {
        Quota result = new Quota();
        result.memory = this.memory + q.memory;
        result.cpu = this.cpu + q.cpu;
        result.disk = this.disk + q.disk;
        return result;
    }

    public Quota decreaseBy(Quota q) {
        Quota result = new Quota();
        result.memory = this.memory - q.memory;
        result.cpu = this.cpu - q.cpu;
        result.disk = this.disk - q.disk;
        return result;
    }

    public boolean hasMoreThan(Quota q) {
        return this.memory >= q.memory && this.cpu >= q.cpu && this.disk >= q.disk;
    }
}
