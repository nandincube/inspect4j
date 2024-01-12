package uk.ac.st_andrews.inspect4j;

public class ParentEntity<T> {
    private T declaration;
    private EntityType entityType;

    public ParentEntity(T declaration, EntityType entityType){
        //this.entity = entity;
        this.declaration = declaration;
        this.entityType = entityType;
    }

    public T getDeclaration(){
        return declaration;
    }

    public EntityType getEntityType(){
        return entityType;
    }
}
