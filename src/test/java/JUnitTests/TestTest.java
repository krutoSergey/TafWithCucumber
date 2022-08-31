package JUnitTests;

import contextMy.TAContext;
import org.junit.jupiter.api.Test;

public class TestTest  extends BaseTest{

    @Test
    public void test(){
        TAContext context = TAContext.getInstance();
        context.getUserContext();
    }
}
