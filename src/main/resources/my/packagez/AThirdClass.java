package my.packagez;

public class AThirdClass {

    public int returnAnInt(){
        return 2;
    }

    public void invoker() {
        AnotherClass anotherClass = new AnotherClass();
        anotherClass.aMethod();
        anotherClass.aMethod(returnAnInt() * 3);
        anotherClass.aMethod("foo");
    }

}