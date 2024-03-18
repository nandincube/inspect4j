package uk.ac.st_andrews.inspect4j;

/**
 * This class represents a parent entity in the AST - class, method, field etc.
 * The parent entity is any entity which contains/is the ancestor node to other
 * entities
 * extracted by inspect4j.
 */
public class ParentEntity<T> {
    private T declaration; // the declaration - class, method, field etc.
    private EntityType entityType; // the entity type of the declaration

    /**
     * Constructor
     * 
     * @param declaration - the declaration
     * @param entityType  - the entity type of the declaration
     */
    public ParentEntity(T declaration, EntityType entityType) {
        this.declaration = declaration;
        this.entityType = entityType;
    }

    /**
     * Gets the declaration
     * 
     * @return T - the declaration
     */
    public T getDeclaration() {
        return declaration;
    }

    /**
     * Gets the entity type of the declaration
     * 
     * @return EntityType - the entity type of the declaration
     */
    public EntityType getEntityType() {
        return entityType;
    }
}
