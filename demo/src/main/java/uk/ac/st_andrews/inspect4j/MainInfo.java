package uk.ac.st_andrews.inspect4j;

public class MainInfo {
    private boolean hasMain;
    private String mainMethod;

    public MainInfo(boolean hasMain, String mainMethod) {
        this.hasMain = hasMain;
        this.mainMethod = mainMethod;
    }
    public boolean hasMain() {
        return hasMain;
    }
    public void setHasMain(boolean hasMain) {
        this.hasMain = hasMain;
    }
    public String getMainMethod() {
        return mainMethod;
    }
    public void setMainMethod(String mainMethod) {
        this.mainMethod = mainMethod;
    }
    
    
}
