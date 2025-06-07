package service;

public class IdGeneratorImpl implements IdGenerator {

    private int id = 1;

    @Override
    public int generateId() {
        return id++;
    }
}
