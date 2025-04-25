package io.beautifulfruit.finalproject.etcd.user;

import java.util.ArrayList;
import java.util.List;

public class DeploymentSuperblock {
    public ArrayList<String> uuids = new ArrayList<>();

    public DeploymentSuperblock(List<String> uuids) {
        if (uuids != null) {
            this.uuids.addAll(uuids);
        }
    }
}