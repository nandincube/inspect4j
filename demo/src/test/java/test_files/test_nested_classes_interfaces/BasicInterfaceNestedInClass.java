package test_files.test_nested_classes_interfaces;

public class BasicInterfaceNestedInClass  {

        public interface nestedInterface {
            void innerMethod();
        }
    
        public class InnerClass implements nestedInterface {
            @Override
            public void innerMethod() {
                System.out.println("Hi");
            }
        }
        public static void main(String[] args) {
            BasicInterfaceNestedInClass nestObj = new BasicInterfaceNestedInClass();
            BasicInterfaceNestedInClass.InnerClass innerObj = nestObj.new InnerClass();
            innerObj.innerMethod();
        }

    
}
