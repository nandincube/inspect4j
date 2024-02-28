package test_files.test_inheritence_type_params_and_superclasses;

public class InterfaceNestedInClass {

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
            InterfaceNestedInClass nestObj = new InterfaceNestedInClass();
            InterfaceNestedInClass.InnerClass innerObj = nestObj.new InnerClass();
            innerObj.innerMethod();
        }

    
}
