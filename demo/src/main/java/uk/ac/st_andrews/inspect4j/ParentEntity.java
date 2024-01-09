package uk.ac.st_andrews.inspect4j;

public class ParentEntity<T, S> {
    private T entity;
    private S declaration;
    private EntityType entityType;

    public ParentEntity(T entity, S declaration, EntityType entityType){
        this.entity = entity;
        this.declaration = declaration;
        this.entityType = entityType;
    }
    
    public T getEntity(){
        return entity;
    }

    public S getDeclaration(){
        return declaration;
    }

    public EntityType getEntityType(){
        return entityType;
    }
}
