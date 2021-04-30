package scanner.analyze;

public class AuthResolve implements Resolve<String> {

    @Override
    public String resolve() {
        return "admin:asdf1234";
    }

}