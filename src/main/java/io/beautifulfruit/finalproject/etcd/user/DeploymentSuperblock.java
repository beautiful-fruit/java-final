package io.beautifulfruit.finalproject.etcd.user;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeploymentSuperblock {
    public ArrayList<UUID> uuids = new ArrayList<>();

    public DeploymentSuperblock(List<UUID> uuids) {
        if (uuids != null) {
            this.uuids.addAll(uuids);
        }
    }
}