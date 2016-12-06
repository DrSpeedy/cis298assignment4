package edu.kvcc.cis298.cis298assignment4.repositories;

import java.util.Map;

/**
 * Created by doc on 11/6/16.
 */

public interface MappedRepository<IdType, Model> {
    Model get(IdType id);
    Map<IdType, Model> getAll();

    void put(IdType id, Model model);
    void putAll(Map<IdType, Model> collection);

    void delete(IdType id);

    int size();
}
