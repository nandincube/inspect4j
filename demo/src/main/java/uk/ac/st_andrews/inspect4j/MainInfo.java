package uk.ac.st_andrews.inspect4j;

/**
 * This class is used to store information about the main method of a class.
 */
public class MainInfo {
    private boolean hasMain; // whether the class has a main method
    private String mainMethod; // the name of the main method

    /**
     *  Constructor
     * @param hasMain - whether the class has a main method
     * @param mainMethod - the name of the main method
     */
    public MainInfo(boolean hasMain, String mainMethod) {
        this.hasMain = hasMain;
        this.mainMethod = mainMethod;
    }

    /**
     *  Method to check if the class has a main method
     * @return boolean - true if the class has a main method, false otherwise
     */
    public boolean hasMain() {
        return hasMain;
    }

    /**
     *  Method to set the hasMain
     * @param hasMain - boolean value to set hasMain
     */
    public void setHasMain(boolean hasMain) {
        this.hasMain = hasMain;
    }

    /**
     *  Method to get the main method
     * @return String - the name of the main method
     */
    public String getMainMethod() {
        return mainMethod;
    }
    
    /** 
     * Method to set the main method
     * @param mainMethod - the name of the main method 
     */
    public void setMainMethod(String mainMethod) {
        this.mainMethod = mainMethod;
    }
    
    
}
