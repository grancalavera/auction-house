package works.quiet.etc;

public interface Validator<T> {

//    make this return a list of all the errors found with the entity
    void validate(T entity) throws Exception;
}

