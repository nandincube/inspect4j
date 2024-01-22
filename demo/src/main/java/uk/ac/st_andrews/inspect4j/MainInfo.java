package uk.ac.st_andrews.inspect4j;

public class MainInfo {
    private boolean hasMain;
    private String mainMethod;

    /**
     * 
     * @param hasMain
     * @param mainMethod
     */
    public MainInfo(boolean hasMain, String mainMethod) {
        this.hasMain = hasMain;
        this.mainMethod = mainMethod;
    }

    /**
     * 
     * @return
     */
    public boolean hasMain() {
        return hasMain;
    }

    /**
     * 
     * @param hasMain
     */
    public void setHasMain(boolean hasMain) {
        this.hasMain = hasMain;
    }

    /**
     * 
     * @return
     */
    public String getMainMethod() {
        return mainMethod;
    }
    
    /**
     * 
     * @param mainMethod
     */
    public void setMainMethod(String mainMethod) {
        this.mainMethod = mainMethod;
    }
    
    
}
