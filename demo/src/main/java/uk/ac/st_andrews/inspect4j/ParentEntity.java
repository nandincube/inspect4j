package uk.ac.st_andrews.inspect4j;

/**
 * 
 */
public class ParentEntity<T> {
    private T declaration;
    private EntityType entityType;

    /**
     * 
     * @param declaration
     * @param entityType
     */
    public ParentEntity(T declaration, EntityType entityType){
        this.declaration = declaration;
        this.entityType = entityType;
    }

    /**
     * 
     * @return
     */
    public T getDeclaration(){
        return declaration;
    }

    /**
     * 
     * @return
     */
    public EntityType getEntityType(){
        return entityType;
    }
}
