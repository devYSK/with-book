package com.apress.springrecipes.nosql;

import org.neo4j.graphdb.RelationshipType;

public enum RelationshipTypes implements RelationshipType {
    FRIENDS_WITH, MASTER_OF, LOCATION
}
